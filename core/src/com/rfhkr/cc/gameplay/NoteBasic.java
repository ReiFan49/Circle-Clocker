package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
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
	protected static final Texture[] baseTexture = new Texture[4];
	private   static Texture[] triCache;
	protected static String  textureFN = "noteFull.png";
	protected static final float pcOFFSET = 0.3333f;
	static float approach = 1.0f;
	// ** ACCESSORS
	/** retrieves the globally used sensor along the subclasses */
	protected static final Circle getBasicSensor() {
		return Objects.isNull(baseSensor) ? (baseSensor = new Circle(0, 0, 1)) : baseSensor;
	}
	protected static final Texture getSingleTexture() {
		return (Objects.isNull(baseTexture[0])) ?
			baseTexture[0] =
				new Texture(Gdx.files.internal(PathResolver.at("noteFull.png").build("core","assets").resolve())) :
			baseTexture[0];
	}
	protected static final Texture[] getTripleTexture() {
		if(Objects.isNull(baseTexture[1])) {
			final String[] fn = new String[]{"noteTail.png","noteBody.png","noteHead.png"};
			for(int i=1;i<=3;i++)
				baseTexture[i] = new Texture(Gdx.files.internal(PathResolver.at(fn[i-1]).build("core","assets").resolve()));
		}
		return (Objects.isNull(triCache)) ?
			triCache = Arrays.copyOfRange(baseTexture,1,4) :
			triCache;
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	protected boolean holdDown,justBonus;
	protected Array<Texture> noteSprite;
	protected byte  slot;
	protected float time_s;
	protected float time_e;
	protected byte  amp;
	protected Twin<Vector2> desigPos;
	protected Twin<Float> hitTime;
	protected Twin<Judgement> judgeResult;
	// ** ACCESSORS
	public byte  getNotePos() { return slot; }
	public float getApproachStartTime() { return time_s; }
	public float getApproachEndTime()   { return time_e; }
	public byte  getNoteAmp() { return amp;  }
	public Judgement getDownJudgement() { return judgeResult.get1st(); }
	public Judgement getUpJudgement() { return judgeResult.get2nd(); }
	public float getHitTime() { return hitTime.get1st(); }
	public float getRlsTime() { return hitTime.get2nd(); }
	protected void setDownJudgement(Judgement j) { judgeResult.set1st(j); }
	protected void setUpJudgement(Judgement j) { judgeResult.set2nd(j); }
	protected NoteBasic setHitTime(float t) { hitTime.set1st(t); return this; }
	protected NoteBasic setRlsTime(float t) { hitTime.set2nd(t); return this; }
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public Judgement checkTolerance(float hitTime) {
		return Arrays.stream(Judgement.values(),1,4).reduce(Judgement.MISS,(pre,cur)-> hitTime > cur.maxTime ? pre : cur);
	}
	/**
	 * performs the <code>noteHit</code> event handling, by replacing {@link AbstractInteract} render method.
	 * @param delta time passed between frame to frame.
	 * @return self, to be passed on {@link #render(float)} method
	 */
	private final <CurrentItem> CurrentItem render0(float delta, CurrentItem self) {
		// TODO: overrides abstract interact onTouch,onHover,render process.
		return self;
	}
	/**
	 * alters the render function that specified by {@link AbstractInteract}
	 * @param delta time passed between frame to frame.
	 * @return self, to allow separated processing between <code>noteHit</code> event handling and animation handling,
	 * which uses {@link #draw(SpriteBatch)} and {@link #update()} method.
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
		this.hitTime = new Twin<>(Float.NEGATIVE_INFINITY);
		this.judgeResult = new Twin<>(null);
		this.desigPos = new Twin<>(Gameplay.posMap.get(Gameplay.now().getMode()).get(slot-1));
		initializeNoteTexture();
		//this.noteSprite = new Texture(Gdx.files.internal(PathResolver.at(textureFN).resolve()));
	}
	// Driver
}
