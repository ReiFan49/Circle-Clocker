package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.rfhkr.cc.*;
import com.rfhkr.cc.errors.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.cc.level.Chart.NoteType;
import com.rfhkr.util.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public class Gameplay extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static Gameplay self;
	private static Vector2 lastCachedSize = new Vector2();
	public static final Setup setup = new Setup();
	// ** ACCESSORS
	public static Gameplay now() {
		Objects.requireNonNull(self, "Cannot retrieve null reference for singleton-ish class");
		return self;
	}
	private static Gameplay now(Gameplay now) {
		return (self = now);
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static Vector2 getCenterScreen() {
		Twin<Integer> s = now().gRef.getSize();
		return lastCachedSize.set(s.getFirst(),s.getSecond());
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private short    combo;
	private Chartset selectedSet;
	private byte     chartIndex;
	private Map<NoteType,Integer> score;
	private Map<Judgement,Integer> judge;
	// ** ACCESSORS
	public Metadata getMetadata() { return selectedSet.getMetadata(); }
	public String   getBG() { return selectedSet.getSongBG(); }
	public String   getSong() { return selectedSet.getSongName(); }
	public Chart    getChart() { return selectedSet.getDifficulties(chartIndex); }
	public int      getMode() {
		int m = getChart().getMode();
		int pwr = m;
		while(pwr > 4)
			if((pwr&1)==0)
				pwr>>>=1;
			else
				throw new RuntimeException(ReiException.invoke("Chart mode ("+m+") is not power amplification of 2"));
		if((pwr&4)==0b100)
			return m;
		else
			throw new RuntimeException(ReiException.invoke("Chart mode is not multiplication of 2 ("+pwr+") with power of 2."));
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public void dispose() {
		super.dispose();
		for(AbstractInteract o : obj)
			o.dispose();
		now(null);
	}
	public void render(float delta) {
		// Clear the screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Update per frame
		super.render(delta);
	}
	public void show   () {
	}
	public void hide   () {
	}
	public void pause  () {
	}
	public void resume () {
	}
	public void resize (int width,int height) {
	}
	// ** METHODS
	public void processStepPre (float delta) {
	}
	public void processStepMain(float delta) {
		for(AbstractInteract o : obj)
			o.render(delta).update();
	}
	public void processStepDraw(float delta,SpriteBatch batch) {
		batch.begin();

		//gRef.font.getDefault().draw(batch, "Circle Clocker", 100, 64, 600, 1, false);
		for(AbstractInteract o : obj)
			o.draw();

		batch.end();
	}
	public void processStepPost(float delta) {
	}
	// <<END>> Instance Structure
	// Nested Classes
	public static final class Setup /* Struct */ {
		// Class Properties
		public static int approach = 4;
	}
	// Constructors
	public Gameplay(final CCMain gRef, Class<? extends InputProcessor> inputClass) {
		super(gRef,inputClass);
		now(this);
	}
	// Driver
}
