package com.rfhkr.util;

/**
 * @author Rei_Fan49
 * @since 2015/06/30
 */
@FunctionalInterface
public interface InputQuadArg<Fi,Se,Th,Fo> {
	// Constant Fields
	// Abstract Methods
	/**
	 * polls input of quad-pair argument.
	 * @param rst (fi rst) argument
	 * @param cond (se cond) argument
	 * @param ird (th ird) argument
	 * @param urth (fo urth) argument
	 * @return determined inside the lambda
	 */
	boolean poll(Fi rst,Se cond,Th ird,Fo urth);
	// Pre-defined Methods
}
