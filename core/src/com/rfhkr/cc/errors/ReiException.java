package com.rfhkr.cc.errors;

import java.util.function.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public class ReiException extends Exception {
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
	private ReiException() { super(); }
	private ReiException(String msg) { super(msg); }
	private ReiException(Throwable cause) { super(cause); }
	private ReiException(String msg,Throwable cause) { super(msg,cause); }
	// Driver
}
