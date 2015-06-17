package com.rfhkr.cc.level;

import com.rfhkr.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/14
 */
public class Note implements Comparable<Note> {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	/** note position */
	private byte pos;
	/** note scoring amplification */
	private byte amp;
	/** approach and end time */
	private Twin<Timing> time;
	/** note type */
	private Chart.NoteType type;
	// ** ACCESSORS
	public byte getPos() { return pos; }
	public byte getAmp() { return amp; }
	public Timing getStart() { return time.get1st(); }
	public Timing getEnd()   { return time.get2nd(); }
	public Chart.NoteType getType() { return type; }
	public Note setPos(byte p) { this.pos=p; return this; }
	public Note setAmp(byte a) { this.amp=a; return this; }
	public Note setStart(Timing t) { this.time.set1st(t); return this; }
	public Note setEnd  (Timing t) { this.time.set2nd(t); return this; }
	public Note setType (Chart.NoteType n) { this.type=n; return this; }
	// ** PREDICATES
	// ** INTERACTIONS
	public int compareTo(Note other) {
		int r;
		if ((r = this.time.get1st().compareTo(other.time.get1st())) != 0) return r;
		if ((r = Byte.compare(this.pos,other.pos)) != 0) return r;
		if ((r = this.time.get2nd().compareTo(other.time.get2nd())) != 0) return r;
		if ((r = Byte.compare(this.amp,other.amp)) != 0) return r;
		return this.type.compareTo(other.type);
	}
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Class
	// Constructors
	// ==> SIMPLE CONSTRUCTOR
	public Note(Chart.NoteType type, long pos, Timing start, Timing end) {
		this(type,(byte)pos,start,end);
	}
	public Note(Chart.NoteType type, byte pos, Timing start, Timing end) {
		this(type,pos,start,end,1);
	}
	public Note(Chart.NoteType type, long pos, Pair<Timing,Timing> time) {
		this(type,(byte)pos,time.toTwin());
	}
	public Note(Chart.NoteType type, byte pos, Pair<Timing,Timing> time) {
		this(type,pos,time.toTwin(),1);
	}
	public Note(Chart.NoteType type, long pos, Twin<Timing> time) {
		this(type,(byte)pos,time);
	}
	public Note(Chart.NoteType type, byte pos, Twin<Timing> time) {
		this(type,pos,time,1);
	}
	// ==> GRAND CONSTRUCTOR
	public Note(Chart.NoteType type, long pos, Timing start, Timing end, long amp) {
		this(type,(byte)pos,start,end,(byte)amp);
	}
	public Note(Chart.NoteType type, byte pos, Timing start, Timing end, byte amp) {
		this(type,pos,Pair.gen(start,end),amp);
	}
	public Note(Chart.NoteType type, long pos, Pair<Timing,Timing> time, long amp) {
		this(type,(byte)pos,time.toTwin(),(byte)amp);
	}
	public Note(Chart.NoteType type, byte pos, Pair<Timing,Timing> time, byte amp) {
		this(type,pos,time.toTwin(),amp);
	}
	public Note(Chart.NoteType type, long pos, Twin<Timing> time, long amp) {
		this(type,(byte)pos,time,(byte)amp);
	}
	// ==> MAIN CONSTRUCTOR
	public Note(Chart.NoteType type, byte pos, Twin<Timing> time, byte amp) {
		this.type=type;this.pos=pos;this.time=time;this.amp=amp;
	}
	// Driver
}
