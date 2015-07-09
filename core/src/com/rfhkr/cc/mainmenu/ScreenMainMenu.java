package com.rfhkr.cc.mainmenu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/24
 */
public class ScreenMainMenu extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
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
	// ** METHODS <Graphic Control>
	public void dispose() {
		super.dispose();
		for(AbstractInteract o : obj)
			o.dispose();
	}
	public void render(float delta) {
		// Clear the screen
		Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
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

		gRef.font.getDefault().draw(batch, "Circle Clocker", 100, 64, 600, 1, false);
		gRef.font.getDefault().draw(batch,
			String.format("GS%s (%2dframes/%4dms)",
				Gameplay.setup.isSpeedG2GF() ? "SONIC" : String.format("%1.1f",Gameplay.setup.approach()),
				Math.round(Gameplay.setup.getApproachTime() * 60),
				Math.round(Gameplay.setup.getApproachTime() * 1000)
			) ,
			100, 112, 600, 1, false);
		gRef.font.getDefault().draw(batch,
			"Press ENTER to play\n" +
			"Press D-key to adjust guide speed" ,
			100, 128, 600, 1, false);
		for(AbstractInteract o : obj)
			o.draw(batch);

		batch.end();
	}
	public void processStepPost(float delta) {
	}
	// <<END>> Instance Structure
	// Constructors
	public ScreenMainMenu(final CCMain gRef,Class<? extends InputProcessor> inputClass) {
		super(gRef,inputClass);
		obj.add(new MainMenuInteractObjTest<>(gRef,200,200,new Rectangle(0,0,32,32)));
	}
	// Driver
}
