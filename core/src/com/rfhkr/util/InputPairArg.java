package com.rfhkr.util;

/**
 * @author Rei_Fan49
 * @since 2015/06/30
 */
@FunctionalInterface
public interface InputPairArg<Fi,Se> {
	// Constant Fields
	// Abstract Methods
	/**
	 * polls input of dual-type argument.
	 * @param rst (fi rst) argument
	 * @param cond (se cond) argument
	 * @return determined inside the lambda
	 */
	boolean poll(Fi rst,Se cond);
	// Pre-defined Methods
}
