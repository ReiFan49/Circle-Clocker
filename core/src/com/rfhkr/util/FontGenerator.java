package com.rfhkr.util;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.sun.istack.internal.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/24
 */
public final class FontGenerator {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	@SafeVarargs
	public static BitmapFont[] createFonts(PathResolver file,String chars,@NotNull Integer... sizeLists) {
		FreeTypeFontGenerator
				fontGen    = new FreeTypeFontGenerator(Gdx.files.internal(file.build("core","assets").resolve()));
		FreeTypeFontGenerator.FreeTypeFontParameter
				fontParam  = new FreeTypeFontGenerator.FreeTypeFontParameter();
		Map<Integer,BitmapFont> fontResult = new TreeMap<>((a,b)->Integer.compare(a,b));
		fontParam.characters = chars;
		for(int size: sizeLists) {
			fontParam.size = size;
			fontResult.put(size, fontGen.generateFont(fontParam));
		}
		return fontResult.values().toArray(new BitmapFont[fontResult.size()]);
	}
	public static BitmapFont[] createFonts(String fileName,String chars,@NotNull Integer... sizeLists) {
		return createFonts(PathResolver.at(fileName),chars,sizeLists);
	}
	// <<END>> Class Structure
	// Constructors
	private FontGenerator() {}
	// Driver
}
