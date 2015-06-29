package com.rfhkr.cc.gameplay;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
enum Judgement {
	// Enumeration List
	MISS  (Float.POSITIVE_INFINITY,1.25f,0),
	BAD   (1.25f,0.70f,100),
	HIT   (0.70f,0.45f,300),
	EXCEL (0.45f,0.00f,500),
	JUST  (0.25f,0.00f, 50);
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
