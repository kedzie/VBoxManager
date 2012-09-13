/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */


package org.kobjects.xml;

import java.io.*;
import java.util.*;

/** A minimalistic XML pull parser, similar to kXML, but
    not supporting namespaces or legacy events. If you need
    support for namespaces, or access to XML comments or
    processing instructions, please use kXML(2) instead. */

public class XmlReader {

    /** Return value of getType before first call to next()  */

    public final static int START_DOCUMENT = 0;

    /** Signal logical end of xml document */

    public final static int END_DOCUMENT = 1;

    /** Start tag was just read */

    public final static int START_TAG = 2;

    /**
     * End tag was just read
     */
    public final static int END_TAG = 3;

    /** Text was just read  */
    public final static int TEXT = 4;

    final static int CDSECT = 5;
    final static int ENTITY_REF = 6;

    static final private String UNEXPECTED_EOF =
        "Unexpected EOF";
    static final private int LEGACY = 999;

    // general

    public boolean relaxed;
    private Hashtable entityMap;
    private int depth;
    private String[] elementStack = new String[4];

    // source

    private Reader reader;

    private char[] srcBuf =
        new char[Runtime.getRuntime().freeMemory() >= 1048576
            ? 8192
            : 128];

    private int srcPos;
    private int srcCount;

    private boolean eof;

    private int line;
    private int column;

    private int peek0;
    private int peek1;

    // txtbuffer

    private char[] txtBuf = new char[128];
    private int txtPos;

    // Event-related

    private int type;
    private String text;
    private boolean isWhitespace;
    private String name;

    private boolean degenerated;
    private int attributeCount;
    private String[] attributes = new String[16];

    private String[] TYPES =
        {
            "Start Document",
            "End Document",
            "Start Tag",
            "End Tag",
            "Text" };

    private final int read() throws IOException {

        int r = peek0;
        peek0 = peek1;

        if (peek0 == -1) {
            eof = true;
            return r;
        }
        else if (r == '\n' || r == '\r') {
            line++;
            column = 0;
            if (r == '\r' && peek0 == '\n')
                peek0 = 0;
        }
        column++;

        if (srcPos >= srcCount) {
            srcCount = reader.read(srcBuf, 0, srcBuf.length);
            if (srcCount <= 0) {
                peek1 = -1;
                return r;
            }
            srcPos = 0;
        }

        peek1 = srcBuf[srcPos++];
        return r;
    }

    private final void exception(String desc)
        throws IOException {
        throw new IOException(
            desc + " pos: " + getPositionDescription());
    }

    private final void push(int c) {
        if (c == 0)
            return;

        if (txtPos == txtBuf.length) {
            char[] bigger = new char[txtPos * 4 / 3 + 4];
            System.arraycopy(txtBuf, 0, bigger, 0, txtPos);
            txtBuf = bigger;
        }

        txtBuf[txtPos++] = (char) c;
    }

    private final void read(char c) throws IOException {
        if (read() != c) {
            if (relaxed) {
                if (c <= 32) {
                    skip(); 
                	read(); 
            	}
            }
            else {
            	exception("expected: '" + c + "'");
            }
        }
    }

    private final void skip() throws IOException {

        while (!eof && peek0 <= ' ')
            read();
    }

    private final String pop(int pos) {
        String result = new String(txtBuf, pos, txtPos - pos);
        txtPos = pos;
        return result;
    }

    private final String readName() throws IOException {

        int pos = txtPos;
        int c = peek0;
        if ((c < 'a' || c > 'z')
            && (c < 'A' || c > 'Z')
            && c != '_'
            && c != ':'
            && !relaxed)
            exception("name expected");

        do {
            push(read());
            c = peek0;
        }
        while ((c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || (c >= '0' && c <= '9')
            || c == '_'
            || c == '-'
            || c == ':'
            || c == '.');

        return pop(pos);
    }

    private final void parseLegacy(boolean push)
        throws IOException {

        String req = "";
        int term;

        read(); // <
        int c = read();

        if (c == '?') {
            term = '?';
        }
        else if (c == '!') {
            if (peek0 == '-') {
                req = "--";
                term = '-';
            }
            else {
                req = "DOCTYPE";
                term = -1;
            }
        }
        else {
            if (c != '[')
                exception("cantreachme: " + c);
            req = "CDATA[";
            term = ']';
        }

        for (int i = 0; i < req.length(); i++)
            read(req.charAt(i));

        if (term == -1)
            parseDoctype();
        else {
            while (true) {
                if (eof)
                    exception(UNEXPECTED_EOF);

                c = read();
                if (push)
                    push(c);

                if ((term == '?' || c == term)
                    && peek0 == term
                    && peek1 == '>')
                    break;
            }
            read();
            read();

            if (push && term != '?')
                pop(txtPos - 1);
        }
    }

    /** precondition: &lt! consumed */

    private final void parseDoctype() throws IOException {

        int nesting = 1;

        while (true) {
            int i = read();
            switch (i) {

                case -1 :
                    exception(UNEXPECTED_EOF);

                case '<' :
                    nesting++;
                    break;

                case '>' :
                    if ((--nesting) == 0)
                        return;
                    break;
            }
        }
    }

    /* precondition: &lt;/ consumed */

    private final void parseEndTag() throws IOException {

        read(); // '<'
        read(); // '/'
        name = readName();
        if (depth == 0 && !relaxed)
            exception("element stack empty");
            
        if (name.equals(elementStack[depth-1]))
        	depth--;
        else if (!relaxed)
            exception("expected: " + elementStack[depth]);
        skip();
        read('>');
    }

    private final int peekType() {
        switch (peek0) {
            case -1 :
                return END_DOCUMENT;
            case '&' :
                return ENTITY_REF;
            case '<' :
                switch (peek1) {
                    case '/' :
                        return END_TAG;
                    case '[' :
                        return CDSECT;
                    case '?' :
                    case '!' :
                        return LEGACY;
                    default :
                        return START_TAG;
                }
            default :
                return TEXT;
        }
    }

    private static final String[] ensureCapacity(
        String[] arr,
        int required) {
        if (arr.length >= required)
            return arr;
        String[] bigger = new String[required + 16];
        System.arraycopy(arr, 0, bigger, 0, arr.length);
        return bigger;
    }

    /** Sets name and attributes */

    private final void parseStartTag() throws IOException {

        read(); // <
        name = readName();
        elementStack = ensureCapacity(elementStack, depth + 1);
        elementStack[depth++] = name;

        while (true) {
            skip();

            int c = peek0;

            if (c == '/') {
                degenerated = true;
                read();
                skip();
                read('>');
                break;
            }

            if (c == '>') {
                read();
                break;
            }

            if (c == -1)
                exception(UNEXPECTED_EOF);

            String attrName = readName();

            if (attrName.length() == 0)
                exception("attr name expected");

            skip();
            read('=');
            
				
            skip();
            int delimiter = read();

            if (delimiter != '\'' && delimiter != '"') {
                if (!relaxed)
                    exception(
                        "<"
                            + name
                            + ">: invalid delimiter: "
                            + (char) delimiter);

                delimiter = ' ';
            }

            int i = (attributeCount++) << 1;

            attributes = ensureCapacity(attributes, i + 4);

            attributes[i++] = attrName;

            int p = txtPos;
            pushText(delimiter);

            attributes[i] = pop(p);

            if (delimiter != ' ')
                read(); // skip endquote
        }
    }

    /** result: isWhitespace; if the setName parameter is set,
    the name of the entity is stored in "name" */

    public final boolean pushEntity() throws IOException {

        read(); // &

        int pos = txtPos;

        while (!eof && peek0 != ';')
            push(read());

        String code = pop(pos);

        read();

        if (code.length() > 0 && code.charAt(0) == '#') {
            int c =
                (code.charAt(1) == 'x'
                    ? Integer.parseInt(code.substring(2), 16)
                    : Integer.parseInt(code.substring(1)));
            push(c);
            return c <= ' ';
        }

        String result = (String) entityMap.get(code);
        boolean whitespace = true;

        if (result == null)
            result = "&" + code + ";";

        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if (c > ' ')
                whitespace = false;
            push(c);
        }

        return whitespace;
    }

    /** types:
    '<': parse to any token (for nextToken ())
    '"': parse to quote
    ' ': parse to whitespace or '>'
    */

    private final boolean pushText(int delimiter)
        throws IOException {

        boolean whitespace = true;
        int next = peek0;

        while (!eof
            && next != delimiter) { // covers eof, '<', '"'

            if (delimiter == ' ')
                if (next <= ' ' || next == '>')
                    break;

            if (next == '&') {
                if (!pushEntity())
                    whitespace = false;

            }
            else {
                if (next > ' ')
                    whitespace = false;

                push(read());
            }

            next = peek0;
        }

        return whitespace;
    }

    //--------------- public part starts here... ---------------


    public XmlReader(Reader reader) throws IOException {
        this.reader = reader;

        peek0 = reader.read();
        peek1 = reader.read();

        eof = peek0 == -1;

        entityMap = new Hashtable();
        entityMap.put("amp", "&");
        entityMap.put("apos", "'");
        entityMap.put("gt", ">");
        entityMap.put("lt", "<");
        entityMap.put("quot", "\"");

        line = 1;
        column = 1;
    }

    public void defineCharacterEntity(
        String entity,
        String value) {
        entityMap.put(entity, value);
    }

    public int getDepth() {
        return depth;
    }

    public String getPositionDescription() {

        StringBuffer buf =
            new StringBuffer(
                type < TYPES.length ? TYPES[type] : "Other");

        buf.append(" @" + line + ":" + column + ": ");

        if (type == START_TAG || type == END_TAG) {
            buf.append('<');
            if (type == END_TAG)
                buf.append('/');

            buf.append(name);
            buf.append('>');
        }
        else if (isWhitespace)
            buf.append("[whitespace]");
        else
            buf.append(getText());

        return buf.toString();
    }

    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return column;
    }

    public boolean isWhitespace() {
        return isWhitespace;
    }

    public String getText() {

        if (text == null)
            text = pop(0);

        return text;
    }

    public String getName() {
        return name;
    }

    public boolean isEmptyElementTag() {
        return degenerated;
    }

    public int getAttributeCount() {
        return attributeCount;
    }

    public String getAttributeName(int index) {
        if (index >= attributeCount)
            throw new IndexOutOfBoundsException();
        return attributes[index << 1];
    }

    public String getAttributeValue(int index) {
        if (index >= attributeCount)
            throw new IndexOutOfBoundsException();
        return attributes[(index << 1) + 1];
    }

    public String getAttributeValue(String name) {

        for (int i = (attributeCount << 1) - 2;
            i >= 0;
            i -= 2) {
            if (attributes[i].equals(name))
                return attributes[i + 1];
        }

        return null;
    }

    public int getType() {
        return type;
    }

    public int next() throws IOException {

        if (degenerated) {
            type = END_TAG;
            degenerated = false;
            depth--;
            return type;
        }

        txtPos = 0;
        isWhitespace = true;

        do {
            attributeCount = 0;

            name = null;
            text = null;
            type = peekType();

            switch (type) {

                case ENTITY_REF :
                    isWhitespace &= pushEntity();
                    type = TEXT;
                    break;

                case START_TAG :
                    parseStartTag();
                    break;

                case END_TAG :
                    parseEndTag();
                    break;

                case END_DOCUMENT :
                    break;

                case TEXT :
                    isWhitespace &= pushText('<');
                    break;

                case CDSECT :
                    parseLegacy(true);
                    isWhitespace = false;
                    type = TEXT;
                    break;

                default :
                    parseLegacy(false);
            }
        }
        while (type > TEXT
            || type == TEXT
            && peekType() >= TEXT);

        isWhitespace &= type == TEXT;

        return type;
    }

    //-----------------------------------------------------------------------------
    // utility methods to mak XML parsing easier ...

    /**
     * test if the current event is of the given type and if the
     * name do match. null will match any namespace
     * and any name. If the current event is TEXT with isWhitespace()=
     * true, and the required type is not TEXT, next () is called prior
     * to the test. If the test is not passed, an exception is
     * thrown. The exception text indicates the parser position,
     * the expected event and the current event (not meeting the
     * requirement.
     *
     * <p>essentially it does this
     * <pre>
     *  if (getType() == TEXT && type != TEXT && isWhitespace ())
     *    next ();
     *
     *  if (type != getType
     *  || (name != null && !name.equals (getName ())
     *     throw new XmlPullParserException ( "....");
     * </pre>
     */
    public void require(int type, String name)
        throws IOException {

        if (this.type == TEXT && type != TEXT && isWhitespace())
            next();

        if (type != this.type
            || (name != null && !name.equals(getName())))
            exception("expected: " + TYPES[type] + "/" + name);
    }

    /**
     * If the current event is text, the value of getText is
     * returned and next() is called. Otherwise, an empty
     * String ("") is returned. Useful for reading element
     * content without needing to performing an additional
     * check if the element is empty.
     *
     * <p>essentially it does this
     * <pre>
     *   if (getType != TEXT) return ""
     *    String result = getText ();
     *    next ();
     *    return result;
     *  </pre>
     */

    public String readText() throws IOException {

        if (type != TEXT)
            return "";

        String result = getText();
        next();
        return result;
    }
}
