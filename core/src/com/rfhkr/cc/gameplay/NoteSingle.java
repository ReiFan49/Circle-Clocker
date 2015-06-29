package com.rfhkr.cc.gameplay;

import com.sun.istack.internal.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/17
 */
class NoteSingle extends NoteBasic implements LateJudgable {
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
	public void initializeNoteTexture() {}
	// <<END>> Instance Structure
	// Constructors
	// -- Simple Constructor (no Amplifier)
	public NoteSingle(int slot,float s) { this(slot,s,1); }
	// -- Grand Constructor (with Amplifier)
	public NoteSingle(int slot,float s,@NotNull int n) {
		super(slot,s,s,n);
	}
	// Driver
}
