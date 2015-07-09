package com.rfhkr.cc.errors;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public class ReiException extends Error {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static ReiException invoke() {
		return new ReiException();
	}
	public static ReiException invoke(String msg) {
		return new ReiException(msg);
	}
	public static ReiException invoke(Throwable cause) {
		return new ReiException(cause);
	}
	public static ReiException invoke(String msg,Throwable cause) {
		return new ReiException(msg,cause);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Constructors
	protected ReiException() { super(); }
	protected ReiException(String msg) { super(msg); }
	protected ReiException(Throwable cause) { super(cause); }
	protected ReiException(String msg,Throwable cause) { super(msg,cause); }
	// Driver
}
