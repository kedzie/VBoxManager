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
 
package org.kobjects.io;

import java.io.*;

public class BoundInputStream extends InputStream {

    int remaining;
    InputStream is;


    public BoundInputStream (InputStream is, int length) {
        this.is = is;
        this.remaining = length;
    }


    public int available () throws IOException {
        int avail = is.available ();
        return avail < remaining ? avail : remaining;
    }


    public int read () throws IOException{

        if (remaining <= 0) return -1;
        remaining--;
        return is.read ();
    }


    public int read (byte [] data, int start, int max) throws IOException {
        if (max > remaining) 
            max = remaining;

        int actual = is.read (data, start, max);

        if (actual > 0) remaining -= actual;

        return actual;
    }


    public void close () {
    	try {
			is.close ();
    	}
    	catch (IOException ignored) {
    	}
    }
}


