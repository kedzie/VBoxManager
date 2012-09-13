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

import java.io.*;
import java.util.Enumeration;

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class PimWriter {
    Writer writer;

    public PimWriter(Writer writer){
        this.writer = writer;
    }


    public void writeEntry (PimItem item) throws IOException {
        writer.write("begin:");
        writer.write(item.getType());
        writer.write("\r\n");
        
        for(Enumeration e = item.fieldNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            for (int i = 0; i < item.getFieldCount(name); i++) {
                PimField field = item.getField(name, i);
                writer.write(name);
                writer.write(':');
                writer.write(field.getValue().toString());
                writer.write("\r\n");            
            }
        }

        writer.write("end:");
        writer.write(item.getType());
        writer.write("\r\n\r\n");
        
    }

}
