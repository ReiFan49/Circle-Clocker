package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.util.*;

/**
 * NoteBasic class makes every object created under this specification have altered way on handling interactive
 * events. Such as <code>onHover</code> events are ignored, and all <code>onTouch</code> events are treated as
 * <code>onNoteHit</code> events.<br>
 * It also enables the object of sending a judgement check after handles <code>onNoteHit</code> events, such as MISS,
 * GOOD, etc.
 * @author Rei_Fan49
 * @since 2015/06/08
 */
abstract class NoteBasic extends AbstractInteract<Circle> implements Judgable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	/** main sensor that will be used against the object */
	protected static Circle  baseSensor;
	protected static Texture baseTexture;
	protected static String   textureFN = "noteFull.png";
	// ** ACCESSORS
	/** retrieves the globally used sensor along the subclasses */
	protected static final Circle getBasicSensor() {
		return Objects.isNull(baseSensor) ? (baseSensor = new Circle(0, 0, 1)) : baseSensor;
	}
	protected static final Texture getBasicTexture() {
		return Objects.isNull(baseTexture) ?
			(baseTexture=new Texture(Gdx.files.internal(PathResolver.at("noteFull.png").build("core","assets").resolve()))) :
			baseTexture;
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	protected Array<Texture> noteSprite;
	protected byte  slot;
	protected float time_s;
	protected float time_e;
	protected byte  amp;
	protected Twin<Judgement> judgeResult;
	// ** ACCESSORS
	public byte  getNotePos() { return slot; }
	public float getApproachStartTime() { return time_s; }
	public float getApproachEndTime()   { return time_e; }
	public byte  getNoteAmp() { return amp;  }
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	/**
	 * performs the <code>noteHit</code> event handling, by replacing {@link AbstractInteract} render method.
	 * @param delta time passed between frame to frame.
	 * @return self, to be passed on {@link #render(float)} method
	 */
	// TODO: overrides abstract interact onTouch,onHover,render process.
	private final <CurrentItem> CurrentItem render0(float delta, CurrentItem self) {
		return self;
	}
	/**
	 * alters the render function that specified by {@link AbstractInteract}
	 * @param delta time passed between frame to frame.
	 * @return self, to allow separated processing between <code>noteHit</code> event handling and animation handling,
	 * which uses {@link #draw()} and {@link #update()} method.
	 */
	public final NoteBasic render(float delta) {
		return render0(delta,this);
	}
	/**
	 *  initialize the texture handling for the specified note, like separating head tail and body, or combine them as
	 *  single object.
	 */
	protected abstract void initializeNoteTexture();
	/**
	 * just hit event handling.
	 */
	public final void onTouchDown(float dx,float dy) {}
	/**
	 * while hit event handling.<br>
	 *   <b>this won't work against non-hold notes</b>
	 */
	public final void onTouchHold(float dx,float dy) {}
	/**
	 * after hit event handling.<br>
	 *   <b>this won't work against non-hold notes</b>
	 */
	public final void onTouchUp  (float dx,float dy) {}
	/**
	 * while hit and movement detected event handling.<br>
	 *   <b>this won't work against non-hold notes</b>
	 */
	public final void onTouchDrag(float dx,float dy) {}
	/** unused event */
	public final void onHoverGet (float dx,float dy) {}
	/** unused event */
	public final void onHoverHold(float dx,float dy) {}
	/** unused event */
	public final void onHoverLost(float dx,float dy) {}
	// <<END>> Instance Structure
	// Constructors
	// -- Simple Constructor (no Amplifier)
	public NoteBasic(int slot,Twin<Float> time) { this(slot,time,1); }
	public NoteBasic(int slot,float s) { this(slot,s,1); }
	public NoteBasic(int slot,float s,float e) { this(slot,Twin.set(s,e)); }
	// -- Grand Constructor (with Amplifier)
	public NoteBasic(int slot,Twin<Float> time,int n) { this(slot,time.get1st(),time.get2nd(),n); }
	public NoteBasic(int slot,float s,int n) { this(slot,s,s,n); }
	public NoteBasic(int slot,float s,float e,@NotNull int n) {
		super(null,null); // i'm really sorry for doing this, i hate super above :>
		this.slot = (byte)Math.max(1,Math.min(16,slot));
		this.pos = new Vector2(Gameplay.getCenterScreen());
		this.touchGeo = getBasicSensor();
		this.time_s = s;
		this.time_e = e;
		this.amp = (byte)Math.max(Math.min(n, Byte.MAX_VALUE), 1);
		this.noteSprite = new Array<>(false,3);
		initializeNoteTexture();
		//this.noteSprite = new Texture(Gdx.files.internal(PathResolver.at(textureFN).resolve()));
	}
	// Driver
}
