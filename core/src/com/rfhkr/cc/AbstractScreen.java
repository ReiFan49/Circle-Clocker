package com.rfhkr.cc;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.gameplay.*;
import com.rfhkr.util.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/25
 */
public abstract class AbstractScreen implements Screen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static transient final Stack<AbstractScreen> requests = new Stack<>();
	private static transient final Map<Class<? extends AbstractScreen>,AbstractScreen> cache = new HashMap<>();
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static AbstractScreen request(AbstractScreen screen) {
		return cache.compute(screen.getClass(),(k,v)->screen);
	}
	public static AbstractScreen request(Class<? extends AbstractScreen> screenClass,Object... constructParam) {
		Class[] classParam = new Class[constructParam.length];
		for (int i = 0; i < constructParam.length; i++)
			classParam[i] = constructParam[i].getClass();
		return cache.compute(screenClass,(k,v)-> {
			try {
				return (v!=null) ? v : screenClass.getConstructor(classParam).newInstance(constructParam);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}
	public static void dispose(Class<? extends AbstractScreen> screenClass) {
		cache.computeIfPresent(screenClass,(k,v)-> {
			v.dispose();
			return null;
		});
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	protected transient final CCMain gRef = CCMain.me();
	protected InputProcessor     UIProcessor;
	protected OrthographicCamera cam;
	protected Array<AbstractInteract> obj;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS <Graphic Control>
	public void dispose() {
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
		// Check Screen Change Request
		processScreenChangeRequest();
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
	public void  requestNewScreen(AbstractScreen screen) {
		screenRequest(request(screen));
	}
	public void  requestNewScreen(Class<? extends AbstractScreen> screen,Object... constructParam) { screenRequest(request(screen,constructParam)); }
	private void screenRequest(AbstractScreen screen) {
		requests.push(screen);
		if(!gRef.inputHandler.getProcessors().contains(screen.UIProcessor,true))
			gRef.inputHandler.addProcessor(screen.UIProcessor);
	}
	private void processScreenChangeRequest() {
		if(requests.size() > 0) {
			Screen ls = gRef.getScreen();
			gRef.inputHandler.removeProcessor(UIProcessor);
			gRef.setScreen(requests.pop());
			if(ls instanceof Gameplay)
				ls.dispose();
		}
	}
	// <<END>> Instance Structure
	// Constructors
	protected AbstractScreen(Class <? extends InputProcessor> inputClass) {
		final Twin<Integer> size = gRef.getSize();
		try {
			UIProcessor = (InputProcessor)inputClass.getDeclaredField("self").get(null);
			gRef.inputHandler.addProcessor(UIProcessor);
		} catch(Exception e) { System.err.printf("%s: %s%n",e,e.getMessage()); }
		this.cam  = new OrthographicCamera();
		this.cam.setToOrtho(true);
		this.obj  = new Array<>();
	}
	// Driver
}
