/* 
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
 * IN THE SOFTWARE. 
 *
 * Copyright (C) 2003, David Li
 */

package org.kobjects.xmlrpc;

import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

import org.kobjects.xml.XmlReader;
import org.kobjects.isodate.IsoDate;
import org.kobjects.base64.Base64;

/**
 * @author David Li
 */
public class XmlRpcParser {

    private XmlReader       parser = null;

    /**
     * @param parser    a XmlReader object
     */
    public XmlRpcParser(XmlReader parser) {
        this.parser = parser;
    }
    
    /**
     * @return Maps XML-RPC structs to java.util.Hashtables
     */
    final private Hashtable parseStruct() throws IOException {
	Hashtable result = new Hashtable();
        int type;
	
        // parser.require(XmlReader.START_TAG, "struct");
        type = nextTag();
	while(type != XmlReader.END_TAG) {
            // parser.require(XmlReader.START_TAG, "member");
            nextTag();
	    // parser.require( XmlReader.START_TAG, "name" );
            String name = nextText();
	    // parser.require( XmlReader.END_TAG, "name" );
	    nextTag();
	    result.put( name, parseValue() ); // parse this member value
	    // parser.require( XmlReader.END_TAG, "member" );
	    type = nextTag();
	}
        // parser.require(XmlReader.END_TAG, "struct");
        nextTag();
	return result;
    }


    final private Object parseValue() throws IOException {
	Object result = "";
        int event;
        
	// parser.require(XmlReader.START_TAG, "value");

	event = parser.next();
        if (event == XmlReader.TEXT) {
            result = parser.getText();
            event  = parser.next();                
        } 
        
        if (event == XmlReader.START_TAG) {
	    String name = parser.getName();
            if(name.equals("array")) {
                result = parseArray();
            } else if(name.equals("struct")) {
                result = parseStruct(); 
            } else {
                if( name.equals("string") ) {
                    result = nextText();
                } else if( name.equals("i4") || name.equals("int") ) {
                    result = new Integer (Integer.parseInt(nextText().trim()));
                } else if( name.equals("boolean") ) {
                    result = new Boolean(nextText().trim().equals("1"));
                } else if(name.equals("dateTime.iso8601")) {
                    result = IsoDate.stringToDate(nextText(), IsoDate.DATE_TIME );
                } else if( name.equals("base64") ) {
                    result = Base64.decode(nextText());
                } else if( name.equals("double") ) {
                    result = nextText();
                }
                // parser.require( XmlReader.END_TAG, name );
                nextTag();
            }
	}

        // parser.require( XmlReader.END_TAG, "value" );
        nextTag();
	return result;
    }

    final private Vector parseArray() throws IOException {
        // parser.require( XmlReader.START_TAG, "array" );
	nextTag();
        // parser.require( XmlReader.START_TAG, "data" );
        int type = nextTag();

	Vector vec = new Vector();
	while( type != XmlReader.END_TAG ) {
	    vec.addElement( parseValue() ); 
            type = parser.getType();
	}

        // parser.require( XmlReader.END_TAG, "data" );
        nextTag();
        // parser.require( XmlReader.END_TAG, "array" );
        nextTag();

	return vec;
    }//end parseArray()


    final private Object parseFault() throws IOException {
        // parser.require( XmlReader.START_TAG, "fault" );
	nextTag();
        Object value = parseValue();
        // parser.require( XmlReader.END_TAG, "fault" );
	nextTag();
        return value;
    }

    final private Object parseParams() throws IOException {
        Vector params = new Vector();
        int type;
        
	// parser.require( XmlReader.START_TAG, "params" );
	type = nextTag();
        
	while(type != XmlReader.END_TAG ) {
	    // parser.require( XmlReader.START_TAG, "param" );
	    nextTag();
	    params.addElement(parseValue());
	    // parser.require( XmlReader.END_TAG, "param" );
	    type = nextTag();
	} 
	
	// parser.require( XmlReader.END_TAG, "params" );
	nextTag();

        return params;
    }

    final public Object parseResponse() throws IOException {
        Object result = null;
        int event;

        nextTag();
        // parser.require(XmlReader.START_TAG, "methodResponse");
        event = nextTag();
        if (event == XmlReader.START_TAG) {
            if ("fault".equals(parser.getName())) {
                result = parseFault();
            } else if ("params".equals(parser.getName())) {
                result = parseParams();
            } 
        } 
        // parser.require(XmlReader.END_TAG, "methodResponse");
        return result;
    }


    final private int nextTag() throws IOException {
        int type = parser.getType();
        type = parser.next();
        if (type == XmlReader.TEXT && parser.isWhitespace()) {
            type = parser.next();
        }
        if (type != XmlReader.END_TAG && type != XmlReader.START_TAG) {
            throw new IOException ("unexpected type: " + type);
        }
        return type;
    }

    final private String nextText() throws IOException {
        int type = parser.getType();

        if (type != XmlReader.START_TAG) {
            throw new IOException ("precondition: START_TAG");
        }

        type = parser.next();

        String result;

        if (type == XmlReader.TEXT) {
            result = parser.getText();
            type = parser.next();
        } else {
            result = "";
        }

        if (type != XmlReader.END_TAG) {
            throw new IOException ("END_TAG expected");
        }

        return result;
    }

}
