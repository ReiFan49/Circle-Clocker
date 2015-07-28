package com.rfhkr.cc.errors;

/**
 * @author Rei_Fan49
 * @since 2015/07/27
 */
public class NoRecordException extends ReiException {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static NoRecordException invoke() {
		return new NoRecordException();
	}
	public static NoRecordException invoke(String msg) {
		return new NoRecordException(msg);
	}
	public static NoRecordException invoke(Throwable cause) {
		return new NoRecordException(cause);
	}
	public static NoRecordException invoke(String msg,Throwable cause) {
		return new NoRecordException(msg,cause);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Classes
	// Constructors
	private NoRecordException() { super(); }
	private NoRecordException(String msg) { super(msg); }
	private NoRecordException(Throwable cause) { super(cause); }
	private NoRecordException(String msg,Throwable cause) { super(msg,cause); }
	// Driver
}
