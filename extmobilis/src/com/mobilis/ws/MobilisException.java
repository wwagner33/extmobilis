/**
 * 
 */
package com.mobilis.ws;

/**
 * @author Paulo Costa
 *
 */
public class MobilisException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3049599226138718086L;
	
	public MobilisException(String message) {
		super(message);
	}
	
	public MobilisException(Throwable e) {
		super(e);
	}
	
	public MobilisException(String message,Throwable e) {
		super(message, e);
	}
	

}
