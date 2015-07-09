package com.rfhkr.cc;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.rfhkr.cc.mainmenu.*;
import com.rfhkr.util.*;

public class CCMain extends Game {
	// Class Structure
	// - Properties
	private static CCMain me;
	// - Accessors
	public static CCMain me() { return me; }
	// - Predicates
	// - Interactions
	// - Methods
	// Instance Structure
	// - Properties
	private Twin<Integer> size;
	public SpriteBatch batch;
	public BitmapFonts font;
	public final InputMultiplexer inputHandler = new InputMultiplexer();
	public final AssetManager     assets       = new AssetManager();
	// - Accessors
	public Twin<Integer> getSize() { return size; }
	// - Predicates
	// - Interactions
	// - Methods <Graphic Control>
	public void create () {
		batch = new SpriteBatch();
		font  = new BitmapFonts();
		size  = Twin.set(1,1);
		Gdx.input.setInputProcessor(inputHandler);
		this.setScreen(new ScreenMainMenu(this, AdapterInputMainMenu.class));
	}
	public void dispose() {
		super.dispose();
		System.out.println("App dispose");
		batch.dispose();
		font.disposeAll();
	}
	public void render () { super.render(); }
	public void pause  () {	super.pause(); System.out.println("Focus - Lost"); }
	public void resume () { super.resume(); System.out.println("Focus - Get" ); }
	public void resize (int width,int height) {
		super.resize(width,height);
		size.setBoth(width,height);
		System.out.printf("Resize request %d,%d%n",width,height);
	}
	// Constructor
	{
		me = this;
	}
}
