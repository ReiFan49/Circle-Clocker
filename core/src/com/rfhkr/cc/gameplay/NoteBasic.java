package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.math.*;
import com.rfhkr.cc.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public abstract class NoteBasic extends AbstractInteract<Circle> {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private Circle baseSensor;
	// ** ACCESSORS
	private final Circle getBasicSensor() {
		return Objects.isNull(baseSensor) ? (baseSensor = new Circle(0, 0, 1)) : baseSensor;
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private byte  slot;
	private float time_s;
	private float time_e;
	private byte  amp;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
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
	}
	// Driver
}