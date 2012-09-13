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

import java.util.*;

/**
 * @author Stefan Haustein
 *
 * 
 */

public class PimField {

    String name;
    Object value;
    Hashtable properties;

    public PimField(PimField orig) {
        this(orig.name);
        if (orig.value instanceof String[]) {
            String[] val = new String[((String[]) orig.value).length];
            System.arraycopy((String[]) orig.value, 0, val, 0, val.length);
            value = val;
        }
        else
            value = orig.value;

        if (orig.properties != null) {
            properties = new Hashtable();
            for (Enumeration e = orig.properties.keys();
                e.hasMoreElements();
                ) {
                String name = (String) e.nextElement();
                properties.put(name, orig.properties.get(name));
            }
        }
    }

    public PimField(String name) {
        this.name = name;
    }

    public Enumeration propertyNames() {
        return properties.keys();
    }

    public void setProperty(String name, String value) {
        if (properties == null) {
            if (value == null)
                return;
            properties = new Hashtable();
        }

        if (value == null)
            properties.remove(name);
        else
            properties.put(name, value);
    }
    /**
     * Method setValue.
     * @param object
     */
    public void setValue(Object object) {
        value = object;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return name
            + (properties != null ? (";" + properties) : "")
            + ":"
            + value;
    }

    public String getProperty(String name) {
        return properties == null ? null : (String) properties.get(name);
    }

    public boolean getAttribute(String name) {
        String s = getProperty("type");
        return s == null ? false : s.indexOf(name) != -1;

    }

    public void setAttribute(String name, boolean value) {
        if (getAttribute(name) == value)
            return;

        String s = getProperty("type");
        if (value) {
            if (s == null || s.length() == 0)
                s = name;
            else
                s = s + name;
        }
        else {
            int i = s.indexOf(name);
            if (i > 0)
                i--;
            if (i != -1)
                s = s.substring(0, i) + s.substring(i + name.length() + 1);
        }

        setProperty("type", s);
    }

}
