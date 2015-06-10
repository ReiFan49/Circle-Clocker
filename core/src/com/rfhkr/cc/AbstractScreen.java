package com.rfhkr.cc;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public abstract class AbstractScreen implements Screen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	protected final CCMain       gRef;
	protected InputProcessor     UIProcessor;
	protected OrthographicCamera cam;
	protected Array<AbstractInteract> obj;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS <Graphic Control>
	public void dispose() {
		gRef.inputHandler.removeProcessor(0);
	}
	public void render(float delta) {
		// Update camera
		cam.update();
		gRef.batch.setProjectionMatrix(cam.combined);
		processStepPre (delta);
		// Draw session
		processStepDraw(delta,gRef.batch);
		processStepMain(delta);
		processStepPost(delta);
	}
	public abstract void show   ();
	public abstract void hide   ();
	public abstract void pause  ();
	public abstract void resume ();
	public abstract void resize (int width,int height);
	// ** METHODS
	public abstract void processStepPre (float delta);
	public abstract void processStepMain(float delta);
	public abstract void processStepDraw(float delta,SpriteBatch batch);
	public abstract void processStepPost(float delta);
	// <<END>> Instance Structure
	// Constructors
	public AbstractScreen(final CCMain gRef,Class<? extends InputProcessor> inputClass) {
		final Twin<Integer> size = gRef.getSize();
		try {
			UIProcessor = inputClass.newInstance();
			gRef.inputHandler.addProcessor(0,UIProcessor);
		} catch(Exception e) {}
		this.gRef = gRef;
		this.cam  = new OrthographicCamera();
		this.cam.setToOrtho(true);
		this.obj  = new Array<>();
	}
	// Driver
}
