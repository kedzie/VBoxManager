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
 
 
package org.kobjects.util;

import java.util.Vector;

public class Csv {

	/** Performs escaping only. Occuring quote characters are duplicated,
	 * 	control characters are escaped by  */

	public static String encode(String value, char quote) {
		
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == quote || c == '^') {
				buf.append(c);
				buf.append(c);
			}
			else if (c < ' ') {
				buf.append('^');
				buf.append((char) (((int) c) + 64));
			}
			else buf.append(c);
		}
		return buf.toString();	
	}


	public static String encode(Object[] values) {
	
		StringBuffer buf = new StringBuffer();
	
		for (int i = 0; i < values.length; i++) {
			if (i != 0) buf.append(',');
			
			Object v = values[i];
			if ((v instanceof Number) || (v instanceof Boolean))
				buf.append (v.toString());
			else {
				buf.append ('"');
				buf.append(encode(v.toString(), '"'));
				buf.append ('"');
			}
		}
		
		return buf.toString ();
	}




	public static String[] decode(String line) {
		Vector tmp = new Vector();

		int p0 = 0;
		int len = line.length();

		//System.out.println (line);

		while (true) {
			// skip spaces

			while (p0 < len && line.charAt(p0) <= ' ')
				p0++;
			if (p0 >= len)
				break;

			if (line.charAt(p0) == '"') {
				p0++;
				// copy this to the mime decoder!
				StringBuffer buf = new StringBuffer();
				while (true) {
					char c = line.charAt(p0++);
                    if (c == '^' && p0 < len) {
                        char c2 = line.charAt (p0++);
                        buf.append (c2 == '^' ? c2 : (char) (((int) c2)-64));
                    }
					else {
                        if (c == '"') {
    						if (p0 == len || line.charAt(p0) != '"')
	       						break;

			     			p0++;
				    	}

                        buf.append(c);
                    }
				}

				tmp.addElement(buf.toString());

				while (p0 < len && line.charAt(p0) <= ' ')
					p0++;
				if (p0 >= len)
					break;
				else if (line.charAt(p0) != ',')
					throw new RuntimeException(
						"Comma expected at " + p0 + " line: " + line);
				p0++;
			}
			else {
				int p1 = line.indexOf(',', p0);
				if (p1 == -1) {
					tmp.addElement(line.substring(p0).trim());
					break;
				}
				tmp.addElement(line.substring(p0, p1).trim());
				p0 = p1 + 1;
			}
		}

		String[] result = new String[tmp.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = (String) tmp.elementAt(i);

		return result;
	}
}