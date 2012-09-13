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

import java.io.*;

public final class Util {

    /** 
     * Writes the contents of the input stream to the 
     * given output stream and closes the input stream.
     * The output stream is returned */

    public static OutputStream streamcopy(InputStream is, OutputStream os)
        throws IOException {
        byte[] buf =
            new byte[Runtime.getRuntime().freeMemory() >= 1048576
                ? 16384
                : 128];
        while (true) {
            int count = is.read(buf, 0, buf.length);
            if (count == -1)
                break;
            os.write(buf, 0, count);
        }
        is.close();
        return os;
    }

    public static int indexOf(Object[] arr, Object find) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(find))
                return i;
        }
        return -1;
    }

    public static String buildUrl(String base, String local) {

        int ci = local.indexOf(':');

        // slash or 2nd char colon: ignore base, return file:///local

        if (local.startsWith("/") || ci == 1)
            return "file:///" + local;

        // local contains colon, assume URL, return local

        if (ci > 2 && ci < 6)
            return local;

        if (base == null)
            base = "file:///";
        else {
            if (base.indexOf(':') == -1)
                base = "file:///" + base;

            if (!base.endsWith("/"))
                base = base + ("/");
        }

        return base + local;
    }

    public static void sort(Object[] arr, int start, int end) {
        //  	System.err.println("sorting: ["+start+", "+end+"[");

        if (end - start <= 2) {
            if (end - start == 2
                && arr[start].toString().compareTo(arr[start + 1].toString())
                    > 0) {
                Object tmp = arr[start];
                arr[start] = arr[start + 1];
                arr[start + 1] = tmp;
            }
            //		  System.err.println("sorted to "+arr[start]+" < "+arr[start+1]);

            return;
        }

        if (end - start == 3) {
            sort(arr, start, start + 2);
            sort(arr, start + 1, start + 3);
            sort(arr, start, start + 2);

            return;
        }

        int middle = (start + end) / 2;

        sort(arr, start, middle);
        sort(arr, middle, end);

        Object[] tmp = new Object[end - start];

        int i0 = start;
        int i1 = middle;

        //	  System.err.println("merging ["+start+", "+middle+", "+end+"[ of "+arr.length);
        for (int i = 0; i < tmp.length; i++) {
            //		  System.err.println("i: "+i+" i0: "+i0+" i1: "+i1);
            if (i0 == middle) {
                tmp[i] = arr[i1++];
            }
            else if (
                i1 == end
                    || arr[i0].toString().compareTo(arr[i1].toString()) < 0) {
                tmp[i] = arr[i0++];
            }
            else {
                tmp[i] = arr[i1++];
            }

        }
        System.arraycopy(tmp, 0, arr, start, tmp.length);
    }


/*
	public static String readLine(InputStream is, byte[] buf) throws IOException{
		int pos = ((int) buf[0]) & 255;
		int end = ((int) buf[1]) & 255;

		if(pos > end) return null;

		StringBuffer result = new StringBuffer();

		while(true){
			if(pos == end){
				end = is.read(buf, 2, Math.min(buf.length-2, 250));
				if(end == -1){
					pos = 1;
					end = 0;
					break;
				}
				pos = 0;
			}
	
			byte c =buf[(pos++)+2];
	
			if(c == 10)
				break;
			if(c != 13)
				result.append((char) c);
		}

		buf[0] = (byte) pos;
		buf[1] = (byte) end;

		return result.toString();
	}
*/
}
