package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.rfhkr.util.*;

import java.util.*;
import java.util.function.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/31
*/
final class TraceLine {
	// <BEGIN> Class Structure
	private static final Twin<Byte> traceLine = Twin.set((byte)0,(byte)0);
	private static float lastClick;
	private static Texture
		mainTexture, clickTexture, mouseTexture;
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static void init() {
		if (Objects.isNull(mainTexture)) {
			mainTexture  = new Texture(Gdx.files.internal(PathResolver.at("dot.png").build("core","assets").resolve()));
			clickTexture = new Texture(Gdx.files.internal(PathResolver.at("clickMark.png").build("core","assets").resolve()));
			mouseTexture = new Texture(Gdx.files.internal(PathResolver.at("cursor.png").build("core","assets").resolve()));
		}
		lastClick = Float.NEGATIVE_INFINITY;
	}
	public static void clear() {
		traceLine.setBoth((byte)0,(byte)0);
	}
	public static void click() {
		lastClick = Gameplay.now().getElapsed();
	}
	public static void set(int i) {
		set((byte)i);
	}
	public static void set(byte i) {
		if(traceLine.get1st() != i)
			traceLine.set2nd(traceLine.get1st()).set1st(i);
	}
	public static void draw(Batch batch) {
		if(Gameplay.now().getElapsed() - lastClick < 0.15)
			batch.draw(clickTexture,Gameplay.input().x - 8,Gameplay.input().y - 8);
		batch.draw(mouseTexture,Gameplay.input().x - 24,Gameplay.input().y - 24);
		if(Math.min(traceLine.get1st(),traceLine.get2nd()) <= 0)
			return;
		final Function<Byte,Vector2> vect  = (p)->Gameplay.posMap.get(Gameplay.now().getChart().getMode()).get(p-1);
		final Function<Byte,Double>  angle = (x)->
			Math.toDegrees(Math.atan2(vect.apply(x).y - 300,vect.apply(x).x - 400));
		Color rv = new Color(batch.getColor());
		batch.setColor(1.0f,0.0f,0.0f,1.0f);
		batch.draw(mainTexture,400,300,0.5f,0.5f,32,4,1,1,Float.valueOf(angle.apply(traceLine.get1st()).toString()),0,0,1,1,false,true);
		batch.setColor(1.0f,0.5f,0.0f,1.0f);
		batch.draw(mainTexture,400,300,0.5f,0.5f,64,2,1,1,Float.valueOf(angle.apply(traceLine.get2nd()).toString()),0,0,1,1,false,true);
		batch.setColor(rv);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Classes
	// Constructors
	TraceLine() {}
	// Driver
}
