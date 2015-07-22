package com.rfhkr.cc.mainmenu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.util.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/24
 */
public class ScreenMainMenu extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static final Map<Metadata,Array<Chart>> chartList = new HashMap<>(16);
	private static int chartId = 0;
	private static Chart[] chary = {};
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static void chartNext() {
		chartId = (chartId+1) % chary.length;
	}
	public static void chartPrev() {
		while (chartId<=0)
			chartId += chary.length;
		chartId--;
	}
	public static Pair<Metadata,Chart> chartGet() {
		List<Chart> c = Arrays.asList(chary);
		Chart cx = c.get(chartId);
		Metadata mx = chartList.keySet().stream().filter((x)->
				chartList.get(x).contains(cx,true)
		).findAny().orElse(null);
		return Pair.gen(mx,cx);
	}
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
		chartList.clear();
		Chartset.cache.stream().map(x->x.toSingleCharts()).forEach(x->chartList.put(x.getX(),x.getY()));
		System.out.println("Chart List:");
		//chartList.keySet().forEach((k) ->
		//		chartList.get(k).forEach((v) ->
		//				System.out.printf("%s - %s [%s]%n",k.getComposer(),k.getTitle(),v.getDiffName())
		//		)
		//);
		chary = chartList.keySet().stream().flatMap(k->Stream.of(chartList.get(k).toArray())).toArray(Chart[]::new);
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
			100, 108, 600, 1, false);
		gRef.font.getDefault().draw(batch,
			"\r\nPress ENTER to play\r\n" +
			"Press Up/Down to adjust guide speed\r\n" +
			"Press Left/Right to switch song\r\n\r\n" +
			String.format("Press A toggle %s AUTOPLAY mode%n%n", Gameplay.autoplay ? "Remove" : "Set" ) +
			String.format("Chart Name:%n%s - %s [%s <%02d|Lv %02d>]%n",
				chartGet().get1st().getComposer(),
				chartGet().get1st().getTitle(),
				chartGet().get2nd().getDiffName(),
				chartGet().get2nd().getMode(),
				chartGet().get2nd().getDiffLevel()
			),
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
		Chartset.detect("resources\\Charts");
	}
	// Driver
}
