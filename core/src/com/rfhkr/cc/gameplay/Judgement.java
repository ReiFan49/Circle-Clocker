package com.rfhkr.cc.gameplay;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
enum Judgement {
	// Enumeration List
	/** marks a judgement as   MISS  and does not give a score */
	MISS  (Float.POSITIVE_INFINITY,0.125f,0),
	/** marks a judgement as   BAD   and give 1/5 of the score */
	BAD   (0.125f,0.070f,100),
	/** marks a judgement as  GREAT  and give 3/5 of the score */
	HIT   (0.070f,0.045f,300),
	/** marks a judgement as PERFECT and give 5/5 of the score */
	EXCEL (0.045f,0.000f,500),
	/** SPECIAL JUDGEMENT =>
	 only applied whenever the player directly change the cursor right before the note comes to
	 hit (good for early check)
	 */
	JUST  (0.025f,0.000f, 50);
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	public final float minTime  ;
	public final float maxTime  ;
	public final int   baseScore;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Constructors
	Judgement() {
		this(Float.POSITIVE_INFINITY,0.0f,0);
	}
	Judgement(float maxTime, float minTime, int baseScore) {
		this.maxTime = Math.max(Math.min(maxTime,Float.POSITIVE_INFINITY),0.0f);
		this.minTime = Math.min(Math.max(minTime,0.0f),Float.POSITIVE_INFINITY);
		this.baseScore = Math.max(Math.min(baseScore,Integer.MAX_VALUE),0);
	}
	// Driver
}
