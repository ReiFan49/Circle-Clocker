package com.rfhkr.cc.level;

/**
 * @author Rei_Fan49
 * @since 2015/06/05
 */
public final class BPMData {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static BPMData on() {
		return new BPMData();
	}
	public static BPMData on(double bpm,int bar) {
		return on(bpm, (byte) bar);
	}
	public static BPMData on(double bpm,byte bar) {
		return new BPMData(bpm, bar);
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
	// ** METHODS
	// <<END>> Instance Structure
	// Constructors
	private BPMData() {}
	private BPMData(double bpm, byte bar) {
		this.bpm = bpm;
		this.bar = bar;
	}
	// Driver
}
