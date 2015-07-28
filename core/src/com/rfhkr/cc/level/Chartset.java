package com.rfhkr.cc.level;

import com.badlogic.gdx.tools.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.errors.*;
import com.rfhkr.cc.gameplay.result.*;
import com.rfhkr.cc.io.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.io.*;
import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Chartset implements Serializable, Comparable<Chartset> {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static final long serialVersionUID = 0x1f2e_3d4c_5b6a_7988L;
	private static final ChartDetector fpdircg = new ChartDetector();
	public static final transient Set<Chartset> cache = new TreeSet<>(Chartset::compareTo);
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
				target = new Chartset(data);
			target.addDifficulty(diff);
		Highscore.get().getScores(diff);
		return target;
	}
	public static Chartset find(Chart diff) {
		return cache.stream().filter((cs)->
				cs.getDifficulties().contains(diff,true)
		).findFirst().orElse(null);
	}
	public static void detect(String dir) {
		cache.clear();
		Highscore.get().clearScores();
		System.runFinalization();
		String p = PathResolver.from(dir).resolve();
		try {
			fpdircg.process(p,null);
		} catch (Exception e) {
			throw ReiException.invoke(e);
		}
	}
	public static Chartset saveSetToFile(@NotNull Chartset cs, String fn) {
		// TODO: how to export and import chart
		return null;
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private transient PathResolver chartPath;
	private String       chartSong;
	private String       chartBG;
	private Array<Chart> difficulties = new Array<>(1);
	private Metadata     chartData = new Metadata();
	// ** ACCESSORS
	public PathResolver getPath() { return chartPath; }
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
	public Chartset     setPath(PathResolver p) {
		chartPath = p;
		return this;
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
	public int compareTo(Chartset other) {
		return Metadata.comparator.compare(this.chartData,other.chartData);
	}
	public boolean equals(Chartset other) {
		return (
			this.getSongName().equals(other.getSongName()) &&
			this.getSongBG().equals(other.getSongBG()) &&
			this.chartData.equals(other.chartData)
		);
	}
	// ** METHODS
	public Pair<Metadata,Array<Chart>> toSingleCharts() {
		return Pair.gen(getMetadata(),difficulties);
	}
	public String toString() {
		return String.format(
			"Chartset@%s ==> %s - %s " +
				"%s", Integer.toHexString(this.hashCode()),
			chartData.isInstrumental() ? chartData.getVocalist() : chartData.getComposer(),chartData.getTitle(),
			difficulties
		);
	}
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.printf("Put CS@%8s in GC.%n",Integer.toHexString(super.hashCode()));
	}
	// <<END>> Instance Structure
	// Nested Class
	static class ChartDetector extends FileProcessor {
		static final OsuFileReader frd1 = new OsuFileReader();
		ChartDetector() {
			addInputSuffix(".osu");
		}
		protected void processFile(Entry entry) throws Exception {
			String fn = entry.inputFile.getPath();
			if( fn.endsWith(".osu") )
				frd1.parse(fn);
			addProcessedFile(entry);
		}
	}
	// Constructors
	public Chartset() {
		cache.add(this);
	}
	public Chartset(Metadata data) {
		setMetadata(data); cache.add(this);
	}
	public Chartset(Metadata data,Chart... diff) {
		setMetadata(data);
		difficulties = Array.with(diff);
		cache.add(this);
	}
	// Driver
}
