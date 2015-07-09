package com.rfhkr.cc;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.rfhkr.cc.mainmenu.*;
import com.sun.istack.internal.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/03
 */
public abstract class AbstractInteract<C extends Shape2D> implements Interactive<C> {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	/** determines how much far a single frame read for a hold to be treated as drag instead */
	protected static float   Drag_threshold = 0.0f;
	/**  */
	protected static Vector2 input = new Vector2();
	protected static CCMain  gRef;
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	protected Vector2 pos     = new Vector2();
	protected boolean hovered = false;
	protected float holdDur   = 0.0f;
	protected C     touchGeo;
	// ** ACCESSORS
	@Nullable
	private Rectangle getRectangle() { return (touchGeo.getClass().equals(Rectangle.class)) ? (Rectangle)(touchGeo) : null;	}
	@Nullable
	private Circle    getCircle()    { return (touchGeo.getClass().equals(Circle.class)) ? (Circle)(touchGeo) : null;	}
	public final float   getX  () { return pos.x; }
	public final float   getY  () { return pos.y; }
	public final Vector2 getPos() { return pos; }
	public AbstractInteract<C> setX(float x) { pos.x = x; return this; }
	public AbstractInteract<C> setY(float y) { pos.y = y; return this; }
	public AbstractInteract<C> setPos(float x,float y) { pos.set(x,y); return this; }
	public AbstractInteract<C> setPos(Vector2 v) { pos.set(v); return this; }
	// ** PREDICATES
	/** determines whether the sensor is rectangle form or not */
	public boolean isRectangle() { return touchGeo.getClass().equals(Rectangle.class); }
	/** determines whether the sensor is circle form or not */
	public boolean isCircle   () { return touchGeo.getClass().equals(Circle.class); }
	/** determines whether the input is inside the sensor or not */
	public boolean isInside() {
		if(AdapterInputMainMenu.verbose)
			if(isRectangle())
				System.out.printf("[%f,%f,%f,%f] -- [%d,%d] -[%f,%f]> [%f,%f]%n",
					getRectangle().x,getRectangle().y,getRectangle().width,getRectangle().height,
					Gdx.input.getX(),Gdx.input.getY(),
					pos.x,pos.y,
					Gdx.input.getX() - pos.x, Gdx.input.getY() - pos.y
				);
		return (isRectangle()||isCircle()) &&
			(isRectangle() ?
			  getRectangle().contains((int)(Gdx.input.getX() - pos.x),(int)(Gdx.input.getY() - pos.y)) :
			 	getCircle()   .contains((int)(Gdx.input.getX() - pos.x),(int)(Gdx.input.getY() - pos.y))
			);
	}
	// ** INTERACTIONS
	// ** METHODS
	public void reset() {
		pos = pos.set(0,0);
		hovered &= false;
		holdDur *= 0.0f;
	}
	/**
	 * renders input handling for basic interactive objects (handles rendering on other method, to make overriding is
	 * possible)
	 * @param delta time passed between frame to frame.
	 * @return self, method chaining purpose
	 */
	public AbstractInteract<C> render(float delta) {
		return render0(delta);
	}
	/**
	 *
	 * @param delta time passed between frame to frame.
	 * @return self, cascades over to {@link #render(float)} method.
	 */
	private AbstractInteract<C> render0(float delta) {
		Vector2 dv = input.set(Gdx.input.getX(),Gdx.input.getY()).sub(pos);
		if(isInside())
			if (Gdx.input.isTouched())     // is touched
				if (Gdx.input.justTouched()) // is just touched
					onTouchDown(dv.x, dv.y);
				else {                       // already touched some frame ago
					if (Math.max(Gdx.input.getDeltaX(), Gdx.input.getDeltaY()) > Drag_threshold)
						onTouchDrag(dv.x, dv.y);
					else
						onTouchHold(dv.x, dv.y);
					holdDur += delta;
				}
			else {                         // not touched
				if (holdDur > 0.0f) {        // previously touched
					holdDur -= holdDur;
					onTouchUp(dv.x, dv.y);
				} else
				if (hovered) onHoverHold(dv.x, dv.y); else onHoverGet(dv.x, dv.y);
				hovered = true;
			}
		else
		if(hovered) {
			onHoverLost(dv.x,dv.y);
			hovered = false;
		}
		return this;
	}
	/** focuses on draw the class itself */
	public abstract void draw(final SpriteBatch batch);
	/** focuses on handling per step processing */
	public abstract void update();
	/** disposes any gdx resources that included on this object unless poolable */
	public abstract void dispose();
	public abstract void onTouchDown(float dx,float dy);
	public abstract void onTouchHold(float dx,float dy);
	public abstract void onTouchUp  (float dx,float dy);
	public abstract void onTouchDrag(float dx,float dy);
	public abstract void onHoverGet (float dx,float dy);
	public abstract void onHoverHold(float dx,float dy);
	public abstract void onHoverLost(float dx,float dy);
	// <<END>> Instance Structure
	// Constructors
	public AbstractInteract(CCMain gRef,C sensor) { this(gRef,new Vector2(),sensor); }
	public AbstractInteract(CCMain gRef,float x,float y,C sensor) { this(gRef,new Vector2(x,y),sensor); }
	public AbstractInteract(CCMain gRef,Vector2 v,C sensor) {
		AbstractInteract.gRef = gRef;
		pos = v;
		touchGeo = sensor;
	}
	// Driver
}
