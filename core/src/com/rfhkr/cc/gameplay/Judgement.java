package com.rfhkr.cc.gameplay;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public enum Judgement {
	// Enumeration List
	/** SPECIAL JUDGEMENT =>
	 only applied whenever the player directly change the cursor right before the note comes to
	 hit (good for early check)
	 */
	JUST  (0.050f,0.000f, 50,true),
	/** marks a judgement as PERFECT and give 5/5 of the score */
	EXCEL (0.080f,0.000f,500,true),
	/** marks a judgement as  GREAT  and give 3/5 of the score */
	HIT   (0.150f,0.080f,300,true),
	/** marks a judgement as   BAD   and give 1/5 of the score */
	BAD   (0.300f,0.150f,100,false),
	/** marks a judgement as   MISS  and does not give a score */
	MISS  (Float.POSITIVE_INFINITY,0.300f,0,false);
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static int compareTop(Judgement lhs,Judgement rhs) {
		return rhs.ordinal() - lhs.ordinal();
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	public final float   minTime  ;
	public final float   maxTime  ;
	public final int     baseScore;
	public final boolean keepCombo;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Constructors
	Judgement() {
		this(Float.POSITIVE_INFINITY,0.0f,0,false);
	}
	Judgement(float maxTime, float minTime, int baseScore, boolean keepCombo) {
		this.maxTime = Math.max(Math.min(maxTime,Float.POSITIVE_INFINITY),0.0f);
		this.minTime = Math.min(Math.max(minTime,0.0f),Float.POSITIVE_INFINITY);
		this.baseScore = Math.max(Math.min(baseScore,Integer.MAX_VALUE),0);
		this.keepCombo = keepCombo;
	}
	// Driver
}
