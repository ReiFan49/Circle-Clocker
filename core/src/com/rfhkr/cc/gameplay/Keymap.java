package com.rfhkr.cc.gameplay;

import com.rfhkr.util.*;

import java.util.function.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/31
 */
final class Keymap {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static final IntUnaryOperator
		mirror = (x) -> size() - (x+1),
		rotate = (x) -> (x+(size()>>1))%(size()),
		flip   = (x) -> mirror.compose(rotate).applyAsInt(x),
		shiftR = (x) -> (x+1) % size(),
		shiftL = (x) -> (x+(size()-1)) % size();
	// ** ACCESSORS
	public static int size() {
		return Gameplay.now().getChart().getMode();
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static int op(int x) {
		IntUnaryOperator op = IntUnaryOperator.identity();
		if(BitOperator.readBit(Gameplay.orientation,0) == 1)
			op = op.andThen(mirror);
		if(BitOperator.readBit(Gameplay.orientation,1) == 2)
			op = op.andThen(flip);
		return op.applyAsInt(x);
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
	// Driver
}
