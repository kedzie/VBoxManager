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
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */


public abstract class PimItem {

    Hashtable fields = new Hashtable();
    
    public static final int TYPE_STRING = 0;
    public static final int TYPE_STRING_ARRAY = 1;
    
    public PimItem () {
    }
    
    public PimItem(PimItem orig) {
    	for (Enumeration e = orig.fields(); e.hasMoreElements();) {
			addField(new PimField((PimField) e.nextElement()));
    	}
    }    

    public Enumeration fieldNames() {
        return fields.keys();
    }
    
    public void addField(PimField field) {
        Vector v = (Vector) fields.get(field.name);

        if (v == null) {
            v = new Vector();
            fields.put(field.name, v);
        }
        
        v.addElement(field);        
    }

	public Enumeration fields() {
		Vector v = new Vector();
		for (Enumeration e = fieldNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			for (Enumeration f = fields(name); f.hasMoreElements();) {
				v.addElement(f.nextElement());
			}		 
		}
		return v.elements();
	}
	
	public Enumeration fields(String name) {
		Vector v = (Vector) fields.get(name);
		if (v == null) v = new Vector();
		return v.elements();
	}
	
	
    public PimField getField(String name, int index) {
        return (PimField) ((Vector) fields.get(name)).elementAt(index);
    }
    
    
    public int getFieldCount(String name) {
        Vector v = (Vector) fields.get(name);
        return v == null ? 0 : v.size();
    }

    public abstract String getType();
    
    public abstract int getArraySize(String name);

	public int getType(String name){
		return getArraySize(name) == -1 ? TYPE_STRING : TYPE_STRING_ARRAY;
	}

	public void removeField(String name, int index) {
		((Vector) fields.get(name)).removeElementAt(index);
	}
	
	public String toString() {
			return getType()+":"+fields.toString();
		}


}
