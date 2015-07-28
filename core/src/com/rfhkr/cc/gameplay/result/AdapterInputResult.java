package com.rfhkr.cc.gameplay.result;

import com.rfhkr.cc.*;
import com.rfhkr.cc.mainmenu.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/23
 */
public class AdapterInputResult extends AbstractInputAdapter {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static final AdapterInputResult self = new AdapterInputResult();
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
	// Nested Classes
	// Constructors
	private AdapterInputResult() {
		funcKeyDown = (k) -> {
			switch(k) {
				case 66:
				case 131:
					ResultScreen rescr = (ResultScreen)CCMain.me().getScreen();
					if(rescr.timePassed > 5)
						rescr.requestNewScreen(ScreenMainMenu.class);
					else
						rescr.timePassed = 5;
					break;
				default:
					System.out.println("Key Down : " + k);
			}
			return true;
		};
	}
	// Driver
}
