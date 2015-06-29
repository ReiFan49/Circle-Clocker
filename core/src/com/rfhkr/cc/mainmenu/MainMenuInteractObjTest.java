package com.rfhkr.cc.mainmenu;

import com.badlogic.gdx.math.*;
import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
class MainMenuInteractObjTest<C extends Shape2D> extends AbstractInteract<C> {
	// <BEGIN> Class Structure
	// ** PROPERTIES
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
	public void draw() {}
	public void update() {}
	public void dispose() {}
	public void onTouchDown(float dx,float dy) {
		System.out.printf("touch IN %+f %+f%n",dx,dy);
		((AbstractScreen)gRef.getScreen()).requestNewScreen(new Gameplay(gRef,AdapterInputGameplay.class));
	}
	public void onTouchHold(float dx,float dy) { System.out.printf("touch ON %+f %+f%n",dx,dy); }
	public void onTouchUp  (float dx,float dy) { System.out.printf("touch NG %+f %+f%n",dx,dy); }
	public void onTouchDrag(float dx,float dy) { System.out.printf("touch DG %+f %+f%n",dx,dy); }
	public void onHoverGet (float dx,float dy) { System.out.printf("hover IN %+f %+f%n",dx,dy); }
	public void onHoverHold(float dx,float dy) { System.out.printf("hover ON %+f %+f%n",dx,dy); }
	public void onHoverLost(float dx,float dy) { System.out.printf("hover NG %+f %+f%n",dx,dy); }
	// <<END>> Instance Structure
	// Constructors
	public MainMenuInteractObjTest(CCMain gRef,C sensor) { this(gRef,new Vector2(), sensor); }
	public MainMenuInteractObjTest(CCMain gRef,float x,float y,C sensor) { this(gRef,new Vector2(x,y),sensor); }
	public MainMenuInteractObjTest(CCMain gRef,Vector2 v,C sensor) { super(gRef,v,sensor); }
	// Driver
}
