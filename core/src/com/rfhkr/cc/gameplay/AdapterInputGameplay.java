package com.rfhkr.cc.gameplay;

import com.rfhkr.cc.*;

import static com.rfhkr.cc.gameplay.CursorHandler.Mode.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public class AdapterInputGameplay extends AbstractInputAdapter {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static boolean verbose = false;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	{
		funcTouchDown = (x,y,p,b)-> {
			CursorHandler.type(CursorHandler.type()==SPREAD ? CROSSED : SPREAD);
			return false;
		};
	}
	// Constructors
	// Driver
}
