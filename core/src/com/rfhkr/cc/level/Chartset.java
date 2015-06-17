package com.rfhkr.cc.level;

import com.badlogic.gdx.utils.*;
import com.sun.istack.internal.*;

import java.io.*;
import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Chartset implements Serializable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public final static Set<Chartset> cache = new TreeSet<>(Chartset::compareSeries);
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static Chartset designate(Metadata data,@Nullable Chart diff) {
		Chartset target = cache.stream().reduce(null,(pre,cur)->
			cur.getMetadata().equals(data) ? cur : pre
		);
		if (!Objects.nonNull(diff))
			System.out.printf("Found chartset %s with specified metadata.%n",target);
		else
			if (Objects.isNull(target))
				target = (new Chartset()).setMetadata(data);
			target.addDifficulty(diff);
		return target;
	}
	public static Chartset saveSetToFile(@NotNull Chartset cs, String fn) {
		return null;
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private String       chartSong;
	private String       chartBG;
	private Array<Chart> difficulties = new Array<>(1);
	private Metadata     chartData = new Metadata();
	// ** ACCESSORS
	public String       getSongName() {
		return chartSong; // .path();
	}
	public String       getSongBG() {
		return chartBG; //.path();
	}
	public Array<Chart> getDifficulties() {
		return difficulties;
	}
	public Chart        getDifficulties(int i) {
		return difficulties.get(i);
	}
	public Metadata     getMetadata() {
		return chartData;
	}
	public Chartset     setSongName(String fn) {
		chartSong = fn; // Gdx.files.internal(PathResolver.at(fn).toString());
		return this;
	}
	public Chartset     setSongBG(String fn) {
		chartBG = fn; // Gdx.files.internal(PathResolver.at(fn).toString());
		return this;
	}
	public Chartset     setMetadata(Metadata mt) {
		this.chartData = mt;
		return this;
	}
	public Chartset     addDifficulty(Chart diff) {
		difficulties.add(diff);
		difficulties.sort(Chart::compare);
		return this;
	}
	// ** PREDICATES
	// ** INTERACTIONS
	public int compareArtist(Chartset other) {
		if (this.chartData.isInstrumental() && other.chartData.isInstrumental())
			return this.chartData.getComposer().compareToIgnoreCase(other.chartData.getComposer());
		else if (this.chartData.isInstrumental()) return -1;
		else if (other.chartData.isInstrumental()) return +1;
		else return this.chartData.getVocalist().compareToIgnoreCase(other.chartData.getVocalist());
	}
	public int compareTitle(Chartset other) {
		return this.chartData.getTitle().compareToIgnoreCase(other.chartData.getTitle());
	}
	public int compareCircle(Chartset other) {
		if (this.chartData.getGroup()==null) return +1;
		else if (other.chartData.getGroup()==null) return -1;
		else
			return this.chartData.getGroup().compareToIgnoreCase(other.chartData.getGroup());
	}
	public int compareSeries(Chartset other) {
		if (this.chartData.getSeries()==null) return -1;
		else if (other.chartData.getSeries()==null) return +1;
		else
			return this.chartData.getSeries().compareToIgnoreCase(other.chartData.getSeries());
	}
	public boolean equals(Chartset other) {
		return (
			this.getSongName().equals(other.getSongName()) &&
			this.getSongBG().equals(other.getSongBG()) &&
			this.chartData.equals(other.chartData)
		);
	}
	// ** METHODS
	public String toString() {
		return String.format(
			"Chartset@%s ==> %s - %s%n" +
				"%s", Integer.toHexString(this.hashCode()),
			chartData.isInstrumental() ? chartData.getVocalist() : chartData.getComposer(),chartData.getTitle(),
			difficulties
		);
	}
	// <<END>> Instance Structure
	// Constructors
	{
		cache.add(this);
	}
	// Driver
}
