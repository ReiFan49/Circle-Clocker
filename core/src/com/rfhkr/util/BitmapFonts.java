package com.rfhkr.util;
import  java.util.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import  com.badlogic.gdx.graphics.g2d.*;
import  com.badlogic.gdx.graphics.g2d.freetype.*;
import  com.sun.istack.internal.NotNull;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public class BitmapFonts {
	// Class Properties
	private static final int defaultIndex = 0;
	private static BitmapFont defaultFont;
	private static BitmapFont getDefaultFont() {
		return Objects.isNull(defaultFont) ? (defaultFont = new BitmapFont()) : defaultFont;
	}
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private int              currentIndex = 0;
	private List<BitmapFont> bmpFonts;
	// ** ACCESSORS
	public BitmapFont       getDefault() { return this.getFonts(defaultIndex); }
	public BitmapFont       getCurrent() { return this.getFonts(currentIndex); }
	public int              getIndex() { return currentIndex; }
	public BitmapFonts      setIndex(int index) {
		currentIndex = Math.max(Math.min(index,bmpFonts.size()-1),0);
		return this;
	}
	public List<BitmapFont> getFonts() { return bmpFonts; }
	public BitmapFont       getFonts(int index) { return bmpFonts.get(index); }
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public BitmapFonts      add(Object o, int... sizes) {
		return add(o.toString(),sizes);
	}
	public BitmapFonts      add(String fn, int... sizes) {
		return add(Gdx.files.internal(fn),sizes);
	}
	public BitmapFonts      add(FileHandle fh, int... sizes) {
		return add(new FreeTypeFontGenerator(fh),sizes);
	}
	public BitmapFonts      add(FreeTypeFontGenerator fg, int... sizes) {
		return add(fg, Arrays.stream(sizes).mapToObj(s -> {
			FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
			p.size = s;
			return p;
		}).toArray(FreeTypeFontGenerator.FreeTypeFontParameter[]::new));
	}
	public BitmapFonts      add(FreeTypeFontGenerator fg, FreeTypeFontGenerator.FreeTypeFontParameter... params) {
		return add(Arrays.stream(params).map(p -> bmpFonts.add(fg.generateFont(p))).toArray(BitmapFont[]::new));
	}
	public BitmapFonts      add(BitmapFont... bmpf) {
		bmpFonts.addAll(Arrays.asList(bmpf));
		return this;
	}
	public BitmapFonts      dispose(int index) {
		if (!getFonts(index).equals(getDefaultFont()))
			getFonts(index).dispose();
		bmpFonts.remove(getFonts(index));
		return setIndex(currentIndex - ((index<currentIndex) ? 0 : 1));
	}
	public BitmapFonts      disposeAll() {
		bmpFonts.stream().forEach(bmpf -> { if(!bmpf.equals(getDefaultFont())) bmpf.dispose(); });
		bmpFonts.clear();
		bmpFonts.add(getDefaultFont());
		return this.setIndex(currentIndex);
	}
	// <<END>> Instance Structure
	// Constructors
	public BitmapFonts() { this(getDefaultFont()); }
	public BitmapFonts(@NotNull BitmapFont... bmpFonts) {
		this.bmpFonts = new ArrayList<>();
		this.bmpFonts.addAll(Arrays.asList(bmpFonts));
	}
	// Driver
}
