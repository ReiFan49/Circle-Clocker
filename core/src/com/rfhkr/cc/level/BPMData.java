package com.rfhkr.cc.level;

import com.rfhkr.util.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/05
 */
public final class BPMData {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static Map<Pair<Double,Byte>,BPMData> cache = new TreeMap<>((a,b)->{
		return Double.compare(a.get1st(),b.get1st());
	});
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static BPMData on() {
		return cache.getOrDefault(Pair.gen(120.0,4),new BPMData());
	}
	public static BPMData on(double bpm,int bar) {
		return on(bpm, (byte) bar);
	}
	public static BPMData on(double bpm,byte bar) {
		return cache.getOrDefault(Pair.gen(bpm,bar),new BPMData(bpm, bar));
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private double bpm = 120.0;
	private byte  bar = 4;
	// ** ACCESSORS
	public double getBPM() { return bpm; }
	public byte   getBar() { return bar; }
	// ** PREDICATES
	// ** INTERACTIONS
	public boolean equals(Object other) {
		return (other != null) && (other instanceof BPMData) && equals((BPMData) other);
	}
	public boolean equals(BPMData other) {
		return Double.compare(this.bpm,other.bpm)==0&&Byte.compare(this.bar,other.bar)==0;
	}
	// ** METHODS
	public String toString() { return String.format("[%s: %5.3fbpm %d/4 bar]",getClass().getSimpleName(),bpm,bar); }
	// <<END>> Instance Structure
	// Constructors
	private BPMData() {}
	private BPMData(double bpm, byte bar) {
		this.bpm = bpm;
		this.bar = bar;
	}
	// Driver
}
