package com.rfhkr.cc;

import com.badlogic.gdx.*;
import com.rfhkr.util.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/30
 */
public abstract class AbstractInputAdapter extends InputAdapter {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static final AbstractInputAdapter self = null;
	public static boolean verbose = false;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	protected InputSoloArg< Integer > funcKeyDown = (kc)->false;
	protected InputSoloArg< Integer > funcKeyUp   = (kc)->false;
	protected InputSoloArg<Character> funcKeyType = ( c)->false;

	protected InputQuadArg<Integer,Integer,Integer,Integer> funcTouchDown = (x,y,p,b)->false;
	protected InputQuadArg<Integer,Integer,Integer,Integer> funcTouchUp   = (x,y,p,b)->false;
	protected InputTrioArg<  Integer , Integer , Integer  > funcTouchDrag =  (x,y,p) ->false;

	protected InputPairArg<Integer,Integer> funcMouseMove = (x,y)->false;
	protected InputSoloArg<    Integer    > funcScroll    =  (n) ->false;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public final boolean keyDown (int kc) {log("kDown",kc); return funcKeyDown.poll(kc);}
	public final boolean keyUp   (int kc) {log("kRlse",kc); return funcKeyUp  .poll(kc);}
	public final boolean keyTyped(char c) {log("kType", c); return funcKeyType.poll(c);}
	public final boolean touchDown(int x,int y,int p,int b) {log("tHit",x,y,p, b ); return funcTouchDown.poll(x,y,p,b);}
	public final boolean touchUp  (int x,int y,int p,int b) {log("tRls",x,y,p, b ); return funcTouchUp  .poll(x,y,p,b);}
	public final boolean touchDragged   (int x,int y,int p) {log("tDrg",x,y,p,'-'); return funcTouchDrag.poll(x,y,p);}
	public final boolean mouseMoved     (int x,int y)       {log("mPos",x,y);       return funcMouseMove.poll(x,y);}
	public final boolean scrolled (int n) {log("mScrl", n); return funcScroll.poll( n);}
	private void log(String s,Object... param) {
		if (verbose)
			System.out.println(
				Stream.concat(Stream.of((Object) s),Arrays.stream(param))
					.map(Object::toString)
					.collect(Collectors.joining(" "))
			);
	}
	// <<END>> Instance Structure
	// Constructors
	protected AbstractInputAdapter() {}
	// Driver
}
