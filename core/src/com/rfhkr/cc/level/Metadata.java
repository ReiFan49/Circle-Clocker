package com.rfhkr.cc.level;

import com.rfhkr.util.*;

import java.util.*;

/**
 * handles chartset metadata
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Metadata {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static boolean unicode = false;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// **** Metadata Style -- Normal : Unicode
	private Twin<String> songName;
	private Twin<String> vocalist; // null for instrumental
	private Twin<String> composer;
	private Twin<String> group;    // null for various artists
	/* EXPLANATION:
		touhou, fairly, or something that refers to somewhat have at least a fanbase i guess, is treated as series
		standard song like "INTRODUCTION FAR EAST OF EAST" is treated as indie, because it doesn't fit to others,
		also "Garakuta Doll Play" is maimai, okay?
		don't forget that we know "FLOWER" is jubeat, so be careful :D
	 */
	private Twin<String> series;   // null if it does not based on a certain series
	// **** Basic Style -- Character Name : Original Song
	/*
	 * ** NULL on original song to treat as original/base arrangement
	 * ** Grouped Character Case:
	 * **** Something related to Opening/Ending ->
	 * ****** SET TO NULL
	 * **** Something related to Gangbang Encounter (1 vs N) ->
	 * ****** give the group name (ex: tsukumo shimai, prismriver shimai)
	 * ** Multiple value rule:
	 * **** GOLDEN RULE ->
	 * ****** ALWAYS PICK THE TOPMOST/EARLIEST ****
	 * **** Touhou case ->
	 * ****** sort from earliest touhou, and then sort from earliest encounter (1,boss,2,boss,...,ex,boss)
	 * **** Kancolle/Anime Standard case ->
	 * ****** sort from earliest song that appears on earliest episode
	 */
	private Twin<String> origin;
	private float firstOffset;
	private Map<Timing,BPMData> timingPoints;
	// ** ACCESSORS
	private String  getPairResult(Pair<String,String> p) {
		return p.getElemCond("",(a,b)->(unicode&&Objects.nonNull(p.get2nd())),null);
	}
	public String getTitle() {
		return getPairResult(songName);
	}
	public String getVocalist() {
		return getPairResult(vocalist);
	}
	public String getComposer() {
		return getPairResult(composer);
	}
	public String getGroup() {
		return getPairResult(group);
	}
	public String getSeries() {
		return getPairResult(series);
	}
	public String getDesignatedCharacter() {
		return Objects.requireNonNull(origin).get1st();
	}
	public String getBaseArrangement() {
		return Objects.requireNonNull(origin).get2nd();
	}
	public float  getOffset() { return firstOffset; }
	public Map<Timing,BPMData> getTimingPoint() {
		return Objects.requireNonNull(timingPoints);
	}
	// ** PREDICATES
	public boolean isInstrumental() {
		return Objects.isNull(vocalist);
	}
	public boolean isIndie() {
		return Objects.isNull(series);
	}
	public boolean isArrangement() {
		return Objects.nonNull(Objects.requireNonNull(origin).get2nd());
	}
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Constructors
	// Driver
}
