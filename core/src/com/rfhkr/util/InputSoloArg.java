package com.rfhkr.util;

/**
 * @author Rei_Fan49
 * @since 2015/06/30
 */
@FunctionalInterface
public interface InputSoloArg<Fi> {
	// Constant Fields
	// Abstract Methods
	/**
	 * polls input of single type argument.
	 * @param rst (fi rst) argument
	 * @return determined inside the lambda
	 */
	boolean poll(Fi rst);
	// Pre-defined Methods
}
