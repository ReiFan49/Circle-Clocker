package com.rfhkr.cc.mainmenu;

import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public class AdapterInputMainMenu extends AbstractInputAdapter {
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
	// Constructors
	{
		funcKeyDown = (x) -> {
			switch(x) {
				case 19:
					Gameplay.setup.approachSucc();
					break;
				case 20:
					Gameplay.setup.approachPred();
					break;
				case 21:
					ScreenMainMenu.chartPrev();
					break;
				case 22:
					ScreenMainMenu.chartNext();
					break;
				case 29:
					Gameplay.autoplay = !Gameplay.autoplay;
					break;
				case 66:
					((AbstractScreen)CCMain.me().getScreen()).requestNewScreen(
						new Gameplay(CCMain.me(),AdapterInputGameplay.class,ScreenMainMenu.chartGet().get2nd())
					);
					break;
				default:
					System.out.println(x);
			}
			return true;
		};
	}
	// Driver
}
