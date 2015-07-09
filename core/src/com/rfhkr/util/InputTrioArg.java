package com.rfhkr.util;

/**
 * @author Rei_Fan49
 * @since 2015/06/30
 */
@FunctionalInterface
public interface InputTrioArg<Fi,Se,Th> {
	// Constant Fields
	// Abstract Methods
	/**
	 * polls input of triple-type argument.
	 * @param rst (fi rst) argument
	 * @param cond (se cond) argument
	 * @param ird (th ird) argument
	 * @return determined inside the lambda
	 */
	boolean poll(Fi rst,Se cond,Th ird);
	// Pre-defined Methods
}
