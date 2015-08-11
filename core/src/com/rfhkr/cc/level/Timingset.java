package com.rfhkr.cc.level;

import com.rfhkr.cc.errors.*;
import com.rfhkr.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/13
 */
public final class Timingset implements Serializable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private final Map<Timing,BPMData> tp = new TreeMap<>(Timing::compare);
	private final Map<Timing,Float  >  offsets = new TreeMap<>(Timing::compare);
	// ** ACCESSORS
	public float getFirstOffset() { return offsets.getOrDefault(Timing.at(0),null); }
	public Map<Timing,BPMData> getTimingTable () { return Collections.unmodifiableMap(tp     ); }
	public Map<Timing,Float  > getTimingOffset() { return Collections.unmodifiableMap(offsets); }
	public Timingset setFirstOffset(float newOffset) {
		float off = newOffset - offsets.get(Timing.at(0));
		offsets.replaceAll((k,ov)->ov+off);
		return this;
	}
	// ** PREDICATES
	public boolean   haveTiming(Timing t) { return tp.keySet().contains(t); }
	/**
	 * checks whether given BPM is between upper and lower limit of the current timing points.
	 * instant return <code>false</code> for empty table
	 * the range is start-end inclusive
	 * @param bpm speed data that need to be checked
	 * @return true if the bpm is between both-inclusive the maximum and minimum of the table, false otherwise
	 */
	public boolean   haveAround(BPMData bpm) { return haveAround(bpm.getBPM()); }
	/**
	 * checks whether given BPM is between upper and lower limit of the current timing points.
	 * instant return <code>false</code> for empty table
	 * the range is start-end inclusive
	 * @param bpm speed data that need to be checked
	 * @return true if the bpm is between both-inclusive the maximum and minimum of the table, false otherwise
	 */
	public boolean   haveAround(double bpm) {
		if(tp.isEmpty()) return false;
		Twin<Double> range = getRange();
		return bpm >= range.get1st() && bpm <= range.get2nd();
	}
	public boolean   equals(Object ts) {
		return (ts!=null) && (ts instanceof Timingset) && equals((Timingset)ts);
	}
	public boolean   equals(/* @NotNull */ Timingset other) {
		return tp.equals(other.tp) && offsets.equals(other.offsets);
	}
	// ** INTERACTIONS
	// ** METHODS
	public BPMData   earliest() { return ((TreeMap<Timing,BPMData>)tp).firstEntry().getValue(); }
	public Twin<Double> getRange() {
		if(tp.isEmpty())
			throw new NoSuchElementException("Timing table is empty, cannot retrieve the range of the table");
		return Twin.set(
				tp.values().stream().min((a,b) -> Double.compare(a.getBPM(),b.getBPM())).get().getBPM(),
				tp.values().stream().max((a,b) -> Double.compare(a.getBPM(),b.getBPM())).get().getBPM()
		);
	}
	public Timingset addTiming(double bpm) { tp.clear(); return this.addTiming(Timing.at(0),bpm); }
	public Timingset addTiming(BPMData v) { tp.clear(); return this.addTiming(Timing.at(0),v); }
	public Timingset addTiming(Timing k,double bpm) { return this.addTiming(k,BPMData.on(bpm,4)); }
	public Timingset addTiming(Timing k,BPMData v) {
		if (k.toFloat(BPMData.on()) < 0)
			throw new RuntimeException(
				ReiException.invoke(
					String.format("Timing cannot be set beyond minus bar (%s %s)",k,k.toFloat(BPMData.on()) )
				)
			);
		Timing prev = ((TreeMap<Timing,?>)tp).floorKey(k);
		tp.put(k,v);
		offsets.put(k,Objects.nonNull(prev) ? Timing.interval(k,prev).toFloat(tp.get(prev))+offsets.get(prev) : 0);
		return this;
	}
	public Timingset delTiming(Timing k) { tp.remove(k); offsets.remove(k); return this; }
	public float     at(Timing t) {
		/** Get Earliest Timing to current cue */
		Map.Entry<Timing,Float> cue = offsets.entrySet().stream().reduce((pre,cur)->
			(Timing.compare(cur.getKey(),t)<0) ? cur : pre
		).get();
		BPMData closestBPM = tp.get(cue.getKey());
		return t.toFloat(cue.getKey(),closestBPM)+cue.getValue();
	}
	public BPMData   bpm(Timing t) {
		Map.Entry<Timing,Float> cue = offsets.entrySet().stream().reduce((pre,cur)->
				(Timing.compare(cur.getKey(),t)<0) ? cur : pre
		).get();
		return tp.get(cue.getKey());
	}
	public Timing    approx(float time) {
		return approx(time,16);
	}
	public Timing    approx(float time, int  maxdiv) {
		return approx(time,(byte)maxdiv);
	}
	public Timing    approx(float time, byte maxdiv) {
		/** Get Closest Timing **/
		Map.Entry<Timing,BPMData> cue = tp.entrySet().stream().reduce((pre,cur)->
			(Timing.interval(cur.getKey(),pre.getKey()).toFloat(pre.getValue()) >
			time-offsets.get(pre.getKey())) ? pre : cur
		).get();
		float closestTiming = offsets.get(cue.getKey());
		/** Precision Check **/
		double realNum = (double)(time - closestTiming) * cue.getValue().getBPM() / 60.0;
		return Timing.valueOf(realNum,maxdiv);
	}
	// <<END>> Instance Structure
	// Constructors
	public Timingset() {
	}
	@SafeVarargs
	public Timingset(double firstOffset, double firstBPM,/* @Nullable */ Pair<Timing,BPMData>... addition) {
		this((float)firstOffset,BPMData.on(firstBPM,4),addition);
	}
	@SafeVarargs
	public Timingset(double firstOffset, BPMData firstBPM,/* @Nullable */ Pair<Timing,BPMData>... addition) {
		addTiming(firstBPM).setFirstOffset((float)firstOffset);
		if(Objects.nonNull(addition) && addition.length > 0)
			Arrays.stream(addition).peek((x)->addTiming(x.get1st(),x.get2nd()));
	}
	// Driver
	public static void main(String... argv) {
		final Timingset tps = new Timingset(0.00f,200);
		Stream.of(0.30f,0.60f,0.90f,0.975f,1.050f,1.125f,1.20f).forEach(t->System.out.println(tps.approx(t)));
		Stream.of(0.125,0.25,0.375,0.5,0.625,0.75,0.875,1.0).forEach(t->System.out.println(Timing.valueOf(t)));
		tps.addTiming(Timing.at(10),150);
		Stream.of(0.5,1.0,1.5,2.0,3.0,5.0,8.0,13.0,21.0,34.0)
			.map(Timing::valueOf).map(tps::at)
			.forEach(System.out::println);
		System.exit(16777216);
	}
}
