package com.rfhkr.cc.mainmenu;

import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;
import com.rfhkr.cc.gameplay.result.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public class AdapterInputMainMenu extends AbstractInputAdapter {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static boolean verbose = false;
	public static final AdapterInputMainMenu self = new AdapterInputMainMenu();
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
	private AdapterInputMainMenu() {
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
				case 29: // A
					Gameplay.autoplay = !Gameplay.autoplay;
					break;
				case 36: // H
					Highscore.REC_NG = !Highscore.REC_NG;
					// Reset the view
					ScreenMainMenu.chartNext();
					ScreenMainMenu.chartPrev();
					break;
				case 37: // I
					Gameplay.orientation = BitOperator.inverseBit(Gameplay.orientation,1);
					break;
				case 43: // O
					Gameplay.orientation = BitOperator.inverseBit(Gameplay.orientation,0);
					break;
				case 46: // R
					Chartset.detect("resources\\Charts");
					CCMain.me().getScreen().show();
					break;
				case 48: // T
					Gameplay.assistTick = !Gameplay.assistTick;
					break;
				case 49: // U
					Metadata.unicode = !Metadata.unicode;
					break;
				case 76:
					ScreenMainMenu.nextScore();
					break;
				case 66:
					(CCMain.me().getScreen()).requestNewScreen(new Gameplay(ScreenMainMenu.chartGet().get2nd()));
					break;
				default:
					System.out.println(x);
			}
			return true;
		};
	}
	// Driver
}
