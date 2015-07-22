package com.rfhkr.cc.level;

import com.badlogic.gdx.utils.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Chart implements Comparable<Chart> {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	public static int compare(Chart c1,Chart c2) {
		if(Objects.isNull(c1) || c1.diffName.get1st() == DiffType.CUSTOM) return -1;
		if(Objects.isNull(c2) || c2.diffName.get1st() == DiffType.CUSTOM) return +1;
		return c1.diffName.get1st().compareTo(c2.diffName.get1st());
	}
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private byte diffLevel;
	private Pair<DiffType,String> diffName;
	private String diffCharter;
	private boolean diff16Hit = false;
	public final Map<Timing,String> animTitle = new TreeMap<>(Timing::compare);
	public final Map<Timing,String> animMsg   = new TreeMap<>(Timing::compare);
	public final Array<Note> chart = new Array<>(1);
	// ** ACCESSORS
	public byte getDiffLevel() {
		return diffLevel;
	}
	public DiffType getDiffType() {
		return diffName.getX();
	}
	public String getDiffName() {
		if(diffName.getX() == DiffType.CUSTOM && Objects.nonNull(diffName.getY()))
			return diffName.getY();
		else
			return diffName.getX().name();
	}
	public int getMode() {
		return diff16Hit ? 16 : 8; /*(diffName.getX() == DiffType.CUSTOM ?
			diff16Hit : (diffName.getX().ordinal() > 1)
		) ? 16 : 8;*/
	}
	public String getDiffCharter() {
		if(Objects.isNull(diffCharter))
			return "Rei_Fan49";
		else if (diffCharter.equals("") || diffCharter.equalsIgnoreCase("Rei_Fan49"))
			return "Unknown";
		else
			return diffCharter;
	}
	public boolean switchMode() { return (this.diff16Hit=!this.diff16Hit); }
	public Chart setLevel(long lv) { this.diffLevel=(byte)lv; return this; }
	public Chart setCharter(@Nullable String user) { this.diffCharter=user; return this; }
	public Chart setDiffType(DiffType d) { this.diffName.setFirst(d); return this; }
	public Chart setDiffName(@Nullable String name) { this.diffName.setSecond(name); return this; }
	// ** PREDICATES
	// ** INTERACTIONS
	public int compareTo(@Nullable Chart other) { return compare(this,other); }
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Classes
	public enum DiffType {
		CUSTOM  (),
		SIMPLE  ('S','M'),
		BASIC   ('B','S'),
		GENERIC ('G','N'),
		COMPLEX ('C','P'),
		ULTIMATE('U','L');
		public final char[] abbr;
		public static DiffType determine(String s) {
			try { return valueOf(s); } catch (IllegalArgumentException e) { return CUSTOM; }
		}
		DiffType (@Nullable char... abbr) {
			this.abbr = abbr;
		}
	}
	public enum NoteType {
		NOTE_NULL(0),
		NOTE_NORM(1),
		NOTE_LONG(2),
		NOTE_SLDE(3);
		public final float scoreMult;
		NoteType (float mult) {
			this.scoreMult = mult;
		}
	}

	// Constructors
	public Chart(DiffType cd,String charter,byte lv) {
		this.diffName = Pair.gen(cd, null);
		this.diffCharter = charter;
		this.diffLevel = lv;
	}
	public Chart(String diff,String charter,byte lv) {
		this.diffName = Pair.gen(DiffType.determine(diff), diff);
		this.diffCharter = charter;
		this.diffLevel = lv;
	}
	// Driver
}
