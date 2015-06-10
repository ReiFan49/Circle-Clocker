package com.rfhkr.cc;

import com.badlogic.gdx.assets.*;
import com.rfhkr.cc.mainmenu.*;
import com.rfhkr.util.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;

public class CCMain extends Game {
	// Class Structure
	// - Properties
	// - Accessors
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
		System.out.println("App dispose");
		batch.dispose();
		font.disposeAll();
	}
	public void render () { super.render(); }
	public void pause  () {	System.out.println("Focus - Lost");}
	public void resume () {
		System.out.println("Focus - Get" );
	}
	public void resize (int width,int height) {
		size.setBoth(width,height);
		System.out.printf("Resize request %d,%d%n",width,height);
	}
}
