package com.rfhkr.cc.level;

import com.badlogic.gdx.files.*;
import com.badlogic.gdx.utils.*;
import com.sun.istack.internal.*;

import java.io.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class Chartset implements Serializable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static Chartset saveSetToFile(@NotNull Chartset cs, String fn) {
		return null;
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private FileHandle   chartSong;
	private FileHandle   chartBG;
	private Array<Chart> difficulties;
	private Metadata     chartData;
	// ** ACCESSORS
	public String       getSongName() {
		return chartSong.path();
	}
	public String       getSongBG() {
		return chartBG.path();
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
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Constructors
	// Driver
}
