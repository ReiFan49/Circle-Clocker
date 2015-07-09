package com.rfhkr.cc.errors;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public class InvalidSequenceException extends ReiException {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static InvalidSequenceException invoke() {
		return new InvalidSequenceException();
	}
	public static InvalidSequenceException invoke(String msg) {
		return new InvalidSequenceException(msg);
	}
	public static InvalidSequenceException invoke(Throwable cause) {
		return new InvalidSequenceException(cause);
	}
	public static InvalidSequenceException invoke(String msg,Throwable cause) {
		return new InvalidSequenceException(msg,cause);
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
	private InvalidSequenceException() { super(); }
	private InvalidSequenceException(String msg) { super(msg); }
	private InvalidSequenceException(Throwable cause) { super(cause); }
	private InvalidSequenceException(String msg,Throwable cause) { super(msg,cause); }
	// Driver
}
