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

package org.kobjects.mime;

import java.io.*;
import java.util.*;
import org.kobjects.base64.*;


public class Decoder {

    InputStream is;
    Hashtable header;
    boolean eof;
    boolean consumed;
    String boundary;
	String characterEncoding;

   char[] buf = new char[256];
   
    // add some kind of buffering here!!!

    private final String readLine() throws IOException {

		int cnt = 0;

        while (true) {
            int i = is.read();
            if (i == -1 && cnt == 0)
                return null;
            else if (i == -1 || i == '\n')
                return new String(buf, 0, cnt);
            else if (i != '\r'){
            	if(cnt >= buf.length){
            		char[] tmp = new char[(buf.length*3)/2];
            		System.arraycopy(buf, 0, tmp, 0, buf.length);
            		buf = tmp;
            	}
            	
            	buf[cnt++] = (char) i;
            }
        }
    }

    /** 
     * The "main" element is returned in the 
     * hashtable with an empty key ("") */

    public static Hashtable getHeaderElements(String header) {

        String key = "";
        int pos = 0;
        Hashtable result = new Hashtable();
        int len = header.length();

        while (true) {
            // skip spaces

            while (pos < len && header.charAt(pos) <= ' ')
                pos++;
            if (pos >= len)
                break;

            if (header.charAt(pos) == '"') {
                pos++;
                int cut = header.indexOf('"', pos);
                if (cut == -1)
                    throw new RuntimeException("End quote expected in " + header);

                result.put(key, header.substring(pos, cut));
                pos = cut + 2;

                if (pos >= len)
                    break;
                if (header.charAt(pos - 1) != ';')
                    throw new RuntimeException("; expected in " + header);
            }
            else {
                int cut = header.indexOf(';', pos);
                if (cut == -1) {
                    result.put(key, header.substring(pos));
                    break;
                }
                result.put(key, header.substring(pos, cut));
                pos = cut + 1;
            }

            int cut = header.indexOf('=', pos);

            if (cut == -1)
                break;

            key = header.substring(pos, cut).toLowerCase().trim();
            pos = cut + 1;
        }
   //     System.out.println("header: "+result);
        
        return result;
    }

	public Decoder(InputStream is, String _bound) throws IOException{
		this(is, _bound, null);
	}

    public Decoder(InputStream is, String _bound, String characterEncoding) throws IOException {

		this.characterEncoding = characterEncoding;
        this.is = is;
        this.boundary = "--" + _bound;

//        StringBuffer buf = new StringBuffer();

        String line = null;
        while (true) {
            line = readLine();
            if (line == null)
                throw new IOException("Unexpected EOF");

      //      System.out.println("line:  '" + line + "'");
      //      System.out.println("bound: '" + boundary + "'");

            if (line.startsWith(boundary))
                break;
//            buf.append(line);
        }

//        data = buf.toString().getBytes();
        if (line.endsWith("--")){
			eof = true;
			is.close();
        }

		consumed = true;
    }


    public Enumeration getHeaderNames() {
        return header.keys();
    }

    public String getHeader(String key) {
        return (String) header.get(key.toLowerCase());
    }

	public String readContent () throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream ();
		readContent (bos);
		String result = characterEncoding == null 
			? new String(bos.toByteArray()) 
			: new String (bos.toByteArray(), characterEncoding);
			
			
		System.out.println("Field content: '"+result+"'");
		return result;
	}
	
	public void readContent (OutputStream os) throws IOException {
		if (consumed) 
			throw new RuntimeException ("Content already consumed!");

		String line = "";


        String contentType = getHeader("Content-Type");
//		System.out.println("header: " + header);
//		System.out.println("Content-Type: "+contentType);
		

        if ("base64".equals(getHeader("Content-Transfer-Encoding"))) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (true) {
                line = readLine();
                if (line == null)
                    throw new IOException("Unexpected EOF");
                if (line.startsWith(boundary))
                    break;

                Base64.decode(line, os);
            }
        }
        else {

            String deli = "\r\n" + boundary;
            int match = 0;

            while (true) {
                int i = is.read();
/*                if (i >= 32 && i <= 127)
                    System.out.print((char) i);
                else
                    System.out.print("#" + i + ";"); */
                if (i == -1)
                    throw new RuntimeException("Unexpected EOF");

                if (((char) i) == deli.charAt(match)) {
                    match++;
                    if (match == deli.length())
                        break;
                }
                else {
                    if (match > 0) {
                        for (int j = 0; j < match; j++)
                            os.write((byte) deli.charAt(j));

                        match = ((char) i == deli.charAt(0)) ? 1 : 0;
                    }
                    if (match == 0) os.write((byte) i);
                }
            }

            line = readLine(); // read crlf and possibly remaining --
        }

        if (line.endsWith("--"))
            eof = true;

		consumed = true;
	}


    public boolean next() throws IOException {

		if(!consumed) 
			readContent(null);

        if (eof)
            return false;


        // read header 

        header = new Hashtable();
        String line;

        while (true) {
            line = readLine();
            if (line == null || line.equals(""))
                break;
            int cut = line.indexOf(':');
            if (cut == -1)
                throw new IOException("colon missing in multipart header line: " + line);

            header.put(
                line.substring(0, cut).trim().toLowerCase(),
                line.substring(cut + 1).trim());
/*
            System.out.println(
                "key: '"
                    + line.substring(0, cut).trim().toLowerCase()
                    + "' value: '"
                    + line.substring(cut + 1).trim());*/

        }

		consumed = false;	
	
        return true;
    }
}