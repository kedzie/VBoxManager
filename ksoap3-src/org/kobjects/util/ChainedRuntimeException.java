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

/** This class is intended to be thrown in an exception handler, e.g.
    in order to wrap an IOException.  It adds the original exception
    stack trace to it's own stack trace output when printed. */

public class ChainedRuntimeException extends RuntimeException {

	Exception chain;

	public static ChainedRuntimeException create (Exception e, String s) {
		try {
		  return ((ChainedRuntimeException) Class.forName 
		  	("org.kobjects.util.ChainedRuntimeExceptionSE").newInstance ())._create (e, s);
		}
		catch (Exception x) {
	//	System.out.println (""+x);
		}
		return new ChainedRuntimeException (e, s);
	}

 
	ChainedRuntimeException () {
	}


	/** creates a new chained runtime exception with 
	additional information */

    ChainedRuntimeException(Exception e, String s) {
		super(((s == null) ? "rethrown" : s) + ": " + e.toString());
		chain = e;
	}

	/*creates a new chained runtime exception from the
	given exception. 

	public ChainedRuntimeException(Exception e) {
		this(e, null);
	}
    */
	
	/** Helper method to preserve stack trace in J2SE */
	
	ChainedRuntimeException _create (Exception e, String s) {
		throw new RuntimeException ("ERR!");	
	}


	/** returns the original exception */

	public Exception getChained() {
		return chain;
	}



	/** prints the own stack trace followed by the stack trace of the
	original exception. */

	public void printStackTrace() {
		super.printStackTrace();
		if (chain != null)
			chain.printStackTrace();
	}
}