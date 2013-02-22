package com.kedzie.vbox.soap;

/**
 * Handles non-soap methods in a <code>KSOAP</code> proxy
 */
public interface CustomMethodHandler {
	
	/**
	 * Get the name of method
	 * @return	name of method
	 */
	public String getMethodName();
	
	/**
	 * Handle the method
	 * @param args	method arguments
	 * @return return value
	 */
	public Object handleMethod(Object...args);

}
