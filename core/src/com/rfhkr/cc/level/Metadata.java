package com.rfhkr.cc.level;

import com.rfhkr.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * handles chartset metadata
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Metadata implements Serializable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static boolean unicode = false;
	public static Comparator<Metadata> comparator = Metadata::compareSeries;
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
	 * ****** EXAMPLE: koishi - hartmann, sanae - last remote, kogasa - night sky ufo, etc.
	 * **** Kancolle/Anime Standard case ->
	 * ****** sort from earliest encounter of the arrangement
	 */
	private Twin<String> origin;
	private Timingset timingPoints;
	private String    genre;
	// ** ACCESSORS
	private boolean  unicodable(Pair<String,String> p) {
		return unicode&&Objects.nonNull(p.get2nd());
	}
	private String   getPairResult(Pair<String,String> p) {
		return p.getElemCond("",(a,b)->(!unicodable(p)),null);
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
	public Timingset getTimingSet() { return timingPoints; }
	public float  getOffset() { return timingPoints.getFirstOffset(); }
	public String getGenre() { return genre; }
	public Map<Timing,BPMData> getTimingPoint() {
		return timingPoints.getTimingTable();
	}
	private Metadata setPairElement(Pair<String,String> p,String e1) { p.set1st(e1); return this;	}
	private Metadata setPairElement(Pair<String,String> p,/* @Nullable */ String e1,/* @Nullable */ String e2) {
		if (Objects.nonNull(e1)) p.set1st(e1); p.set2nd(e2); return this;
	}
	public Metadata setTitle(String title) { return setPairElement(songName,title); }
	public Metadata setTitle(/* @Nullable */ String title,/* @Nullable */ String unicode) {
		return setPairElement(songName,title,unicode);
	}
	public Metadata setVocalist(String vocal) { return setPairElement(vocalist,vocal); }
	public Metadata setVocalist(/* @Nullable */ String vocal,/* @Nullable */ String unicode) {
		return setPairElement(vocalist,vocal,unicode);
	}
	public Metadata setComposer(String cmp) { return setPairElement(composer,cmp); }
	public Metadata setComposer(/* @Nullable */ String cmp,/* @Nullable */ String unicode) {
		return setPairElement(composer,cmp,unicode);
	}
	public Metadata setGroup(String circle) { return setPairElement(group,circle); }
	public Metadata setGroup(/* @Nullable */ String circle,/* @Nullable */ String unicode) {
		return setPairElement(group,circle,unicode);
	}
	public Metadata setSeries(String origin) { return setPairElement(series,origin); }
	public Metadata setSeries(/* @Nullable */ String origin,/* @Nullable */ String unicode) {
		return setPairElement(series,origin,unicode);
	}
	public Metadata setTimingSet(Timingset t) { timingPoints=t; return this; }
	public Metadata setDesignatedCharacter(String chara) { origin.set1st(chara); return this; }
	public Metadata setBaseArrangement(String baseArr) { origin.set2nd(baseArr); return this; }
	public Metadata setGenre(String g) { genre = g; return this; }
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
	public boolean equals(Object other) {
		return (other != null) && (other instanceof Metadata) && equals((Metadata) other);
	}
	public boolean equals(/* @NotNull */ Metadata other) {
		return (
			this.songName.equals(other.songName) &&
			this.vocalist.equals(other.vocalist) &&
			this.composer.equals(other.composer) &&
			this.group.equals(other.group) &&
			this.series.equals(other.series) &&
			this.origin.equals(other.origin) &&
			this.timingPoints.equals(other.timingPoints)
		);
	}
	// ** INTERACTIONS
	public int compareArtist(Metadata other) {
		if (this.isInstrumental() && other.isInstrumental())
			return this.getComposer().compareToIgnoreCase(other.getComposer());
		else if (this.isInstrumental()) return -1;
		else if (other.isInstrumental()) return +1;
		else
			return this.getVocalist().compareToIgnoreCase(other.getVocalist());
	}
	public int compareTitle(Metadata other) {
		return this.getTitle().compareToIgnoreCase(other.getTitle());
	}
	public int compareCircle(Metadata other) {
		if (this.getGroup()==null && other.getGroup()==null)
			return 0;
		else if (this.getGroup()==null) return +1;
		else if (other.getGroup()==null) return -1;
		else
			return this.getGroup().compareToIgnoreCase(other.getGroup());
	}
	public int compareSeries(Metadata other) {
		if (this.getSeries()==null && other.getSeries()==null)
			return 0;
		else if (this.getSeries()==null) return -1;
		else if (other.getSeries()==null) return +1;
		else
			return this.getSeries().compareToIgnoreCase(other.getSeries());
	}
	// ** METHODS
	public String getArtist() {
		String[] a = {
			getGroup(),
			getVocalist(),
			getComposer()
		};
		return Stream.of(a).filter(Objects::nonNull).filter(x ->x.length() > 0).findFirst().orElse(null);
	}
	public String toStandardFormat() {
		if(getArtist()!=null && getTitle()!=null)
			return String.format(
				"%s - %s",
				getArtist(),getTitle()
			);
		else {
			System.out.println("Artist null? "+Objects.isNull(getArtist()));
			System.out.println("Title  null? "+Objects.isNull(getTitle()));
			return (getArtist() == null) ? getTitle() : getArtist();
		}
	}
	public String toString() {
		return String.format(
			"Metadata@%s==>%n" +
				"\tTitle\t%s%s%n" +
				"\tVocal\t%s%s%n" +
				"\tComposer\t%s%s%n" +
				"\tGroup\t%s%s%n" +
				"\tSeries\t%s%s%n" +
				"\tOrigin\t%s from %s%n", Integer.toHexString(this.hashCode()),
			this.songName.get1st(), unicodable(this.songName) ? " (" + this.songName.get2nd() + ")" : "",
			this.vocalist.get1st(), unicodable(this.vocalist) ? " (" + this.vocalist.get2nd() + ")" : "",
			this.composer.get1st(), unicodable(this.composer) ? " (" + this.composer.get2nd() + ")" : "",
			this.group.get1st(), unicodable(this.group) ? " (" + this.group.get2nd() + ")" : "",
			this.series.get1st(), unicodable(this.series) ? " (" + this.series.get2nd() + ")" : "",
			this.origin.get1st(), this.origin.get2nd()
		);
	}
	// <<END>> Instance Structure
	// Constructors
	{
		songName = Twin.set("",null);
		vocalist = Twin.set("",null);
		composer = Twin.set("",null);
		group    = Twin.set("",null);
		series   = Twin.set("",null);
		origin   = Twin.set(null,null);
		timingPoints = new Timingset();
		genre    = "TEST SONG";
	}
	// Driver
}
