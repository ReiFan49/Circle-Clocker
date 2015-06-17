package com.rfhkr.cc.level.convert;

import com.rfhkr.cc.level.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/10
 */
public interface Convertable {
	// Constant Fields
	// Abstract Methods
	/** allows conversion of a imported chart class
	 * @return converted chart from other rhythm game format into circle clocker format
	 */
	Chart convert();
	// Pre-defined Methods
}
