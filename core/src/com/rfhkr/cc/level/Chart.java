package com.rfhkr.cc.level;

import com.rfhkr.util.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Chart {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private byte diffLevel;
	private Pair<DiffType,String> diffName;
	private String diffCharter;
	private boolean diff16Hit = false;
	private Map<Float,String> animTitle;
	private Map<Float,String> animMsg;
	private Map<Pair<Timing,Byte>,NoteType> chart;
	// ** ACCESSORS
	public byte getDiffLevel() {
		return diffLevel;
	}
	public DiffType getDiffType() {
		return diffName.getX();
	}
	public String getDiffName() {
		if(diffName.getX() == DiffType.CUSTOM)
			return diffName.getY();
		else
			return diffName.getX().name();
	}
	public int getMode() {
		return (diffName.getX() == DiffType.CUSTOM ?
			diff16Hit : (diffName.getX().ordinal() > 1)
		) ? 16 : 8;
	}
	public String getDiffCharter() {
		if(Objects.isNull(diffCharter))
			return "Rei_Fan49";
		else if (diffCharter.equals("") || diffCharter.equalsIgnoreCase("Rei_Fan49"))
			return "Unknown";
		else
			return diffCharter;
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Classes
	public enum DiffType { CUSTOM, SIMPLE, BASIC, GENERIC, COMPLEX, ULTIMATE }
	public enum NoteType { NOTE_NULL, NOTE_NORM, NOTE_LONG, NOTE_SLDE }
	// Constructors
	private Chart(DiffType cd,String charter,byte lv) {
		this.diffName = Pair.gen(cd, null);
		this.diffCharter = charter;
		this.diffLevel = lv;
	}
	private Chart(String diff,String charter,byte lv) {
		this.diffName = Pair.gen(DiffType.CUSTOM, diff);
		this.diffCharter = charter;
		this.diffLevel = lv;
	}
	// Driver
}
