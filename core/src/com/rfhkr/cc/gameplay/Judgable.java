package com.rfhkr.cc.gameplay;

import com.rfhkr.cc.errors.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/17
 */
interface Judgable {
	// Constant Fields
	// Abstract Methods
	// Pre-defined Methods
	default Judgement checkEarliness(float hitTime) {
		if (!(this instanceof EarlyJudgable))
			throw new RuntimeException(ReiException.invoke(String.format("This note %s not early-judgeable check",this)));
		else
			return Judgement.MISS;
	}
	default Judgement checkLateness (float hitTime) {
		if (!(this instanceof LateJudgable))
			throw new RuntimeException(ReiException.invoke(String.format("This note %s not late-judgeable check",this)));
		else
			return Judgement.MISS;
	}
	default Judgement combinedJudge (float earlyHit, float lateHit) {
		if ((this instanceof EarlyJudgable)&&(this instanceof LateJudgable))
			return earlyHit>lateHit ? checkEarliness(earlyHit) : checkLateness(lateHit);
		else
			throw new RuntimeException(
				ReiException.invoke("The object must implements both early and late judgement check!")
			);
	}
}

