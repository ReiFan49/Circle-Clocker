package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public class AdapterInputGameplay extends InputAdapter {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static boolean verbose = false;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public boolean keyDown (int kc) {log("kDown",kc); return super.keyDown (kc);}
	public boolean keyUp   (int kc) {log("kRlse",kc); return super.keyUp   (kc);}
	public boolean keyTyped(char c) {log("kType", c); return super.keyTyped( c);}
	public boolean touchDown(int x,int y,int p,int b) {log("tHit",x,y,p, b ); return super.touchDown (x,y,p,b);}
	public boolean touchUp  (int x,int y,int p,int b) {log("tRls",x,y,p, b ); return super.touchUp   (x,y,p,b);}
	public boolean touchDragged   (int x,int y,int p) {log("tDrg",x,y,p,'-'); return super.touchDragged(x,y,p);}
	public boolean mouseMoved     (int x,int y)       {log("mPos",x,y);       return super.mouseMoved    (x,y);}
	public boolean scrolled (int n) {log("mScrl", n); return super.scrolled( n);}
	public void log(String s,Object... param) {
		if (verbose)
			System.out.println(
				Stream.concat(Stream.of((Object) s), Arrays.stream(param))
					.map(o -> o.toString())
					.collect(Collectors.joining(" "))
			);
	}
	// <<END>> Instance Structure
	// Constructors
	// Driver
}
