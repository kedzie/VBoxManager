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

package org.kobjects.pim;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

import org.kobjects.io.LookAheadReader;

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class PimParser {

	LookAheadReader reader;
	Class type;

	public PimParser(Reader reader, Class type) {
		this.reader = new LookAheadReader(reader);
		this.type = type;
	}

	public PimItem readItem() throws IOException {

		String beg = readName();
		if (beg == null)
			return null;

		if (!beg.equals("begin"))
			throw new RuntimeException("'begin:' expected");

		PimItem item;
		try {
			item = (PimItem) type.newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

        reader.read();

		if (!item.getType().equals(readStringValue().toLowerCase()))
			throw new RuntimeException("item types do not match!");

		while (true) {
			String name = readName();
			if (name.equals("end"))
				break;

			PimField field = new PimField(name);
			readProperties(field);
			Object value;
			switch (item.getType(name)) {
				case PimItem.TYPE_STRING_ARRAY :
					value = readArrayValue(item.getArraySize(name));
					break;
				default :
					value = readStringValue();
			}
			field.setValue(value);
			System.out.println("value:" + value);
			item.addField(field);
		}

        reader.read();
		System.out.println("end:" + readStringValue());

		return item;
	}

	String readName() throws IOException {
		String name = reader.readTo(":;").trim().toLowerCase();
		System.out.println("name:" + name);
		return reader.peek(0) == -1 ? null : name;
	}

	String[] readArrayValue(int size) throws IOException {
		Vector values = new Vector();

		StringBuffer buf = new StringBuffer();
		boolean stay = true;
		do {
			buf.append(reader.readTo(";\n\r"));
			switch (reader.read()) {
				case ';' :
					values.addElement(buf.toString());
					buf.setLength(0);
					break;
				case '\r' :
					if (reader.peek(0) == '\n')
						reader.read();
				case '\n' :
					if (reader.peek(0) != ' ')
						stay = false;
					else
						reader.read();
			}
		}
		while (stay);

		if (buf.length() != 0)
			values.addElement(buf.toString());

		String[] ret = new String[size];
		for (int i = 0; i < Math.min (ret.length, values.size()); i++) {
			ret[i] = (String) values.elementAt(i);
		}
		return ret;
	}

	String readStringValue() throws IOException {
		String value = reader.readLine();
		while (reader.peek(0) == 32) {
			reader.read();
			value = value + reader.readLine();
		}
		return value;
	}

	void readProperties(PimField field) throws IOException {
        int c = reader.read();

        while(c == ' ') {
            c=reader.read();
        }
        
        while (c != ':') {
            String name = reader.readTo(":;=").trim().toLowerCase();
            c = reader.read();
            if (c == '=') {
                field.setProperty(name, reader.readTo(":;").trim().toLowerCase());
                c = reader.read();
            }
            else {
                field.setAttribute(name, true);
            }
        }
	}

}
