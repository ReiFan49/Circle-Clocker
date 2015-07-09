package com.rfhkr.cc;

import com.badlogic.gdx.math.*;

/**
 * interface that allows an object could interact with the collisable
 * @author Rei_Fan49
 * @since 2015/06/03
 */
public interface Interactive<C extends Shape2D> {
	// Constant Fields
	// Abstract Methods
	/** checks whether the input is hovers/touches the object or not */
	boolean isInside();
	/** renders the process of the object to make sure the event is fired at the correct time */
	Interactive<C> render(float delta);
	/** an event that fires whenever the object is just touched down */
	void onTouchDown(float dx,float dy);
	/** an event that fires whenever the object kept touched over */
	void onTouchHold(float dx,float dy);
	/** an event that fires whenever the object is released from the touch */
	void onTouchUp  (float dx,float dy);
	/** an event that fires whenever the object is dragged while being held down */
	void onTouchDrag(float dx,float dy);
	/** an event that fires whenever the object just hovered over, but not touched */
	void onHoverGet (float dx,float dy);
	/** an event that fires whenever the object still hovers, but not touched */
	void onHoverHold(float dx,float dy);
	/** an event that fires whenever the object just unfocused, but not touched */
	void onHoverLost(float dx,float dy);
	// Pre-defined Methods
}
