package com.rfhkr.cc.mainmenu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;
import com.rfhkr.cc.gameplay.result.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.util.*;

import java.util.*;
import java.util.stream.*;

import static com.rfhkr.cc.CCMain.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/24
 */
public class ScreenMainMenu extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static final Map<Metadata,Array<Chart>> chartList = new TreeMap<>(Metadata.comparator);
	private static int chartId = 0;
	private static Chart[] chary = {};
	private static int hsPage = 1;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static void chartNext() {
		chartId = (chartId+(hsPage = 1)) % chary.length;
	}
	public static void chartPrev() {
		while (chartId<=(hsPage = 1)-1)
			chartId += chary.length;
		chartId--;
	}
	public static void nextScore() {
		hsPage = (hsPage % (int)Math.ceil(Highscore.get().getScores(chary[chartId]).size()/5D)) + 1;
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
		Chartset.cache.stream().map(Chartset::toSingleCharts).forEach(x -> chartList.put(x.getX(),x.getY()));
		if(false)
			System.out.println("Chart List:");
			chartList.keySet().forEach((k) ->
				chartList.get(k).forEach((v) ->
					System.out.printf("%s - %s [%s-%02d Lv%02d]%n",k.getComposer(),k.getTitle(),v.getDiffName(),v.getMode(),v.getDiffLevel())
				)
			);
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

		gRef.font.getCurrent().draw(batch, "Circle Clocker", 100, 64, 600, 1, false);

		if(Gameplay.setup.isSpeedG2GF())
			gRef.font.getCurrent().setColor(1,0.6f,0.2f,1);
		gRef.font.getCurrent().draw(batch,
			String.format("GS%s (%2dframes/%4dms)%s",
				Gameplay.setup.isSpeedG2GF() ? "SONIC" : String.format("%1.1f",Gameplay.setup.approach()),
				Math.round(Gameplay.setup.getApproachTime() * 60),
				Math.round(Gameplay.setup.getApproachTime() * 1000),
				Gameplay.setup.isSpeedG2GF() ? "\n"+Judgement.JUST+" automatically awarded for every "+Judgement.EXCEL+"s get." : ""
			),
			100, 108, 600, 1, false
		);
		gRef.font.getCurrent().setColor(1,1,1,1);

		gRef.font.getCurrent().draw(batch,
			"\r\nPress ENTER to play\r\n" +
				"Press Up/Down to adjust guide speed\r\n" +
				"Press Left/Right to switch song\r\n\r\n" +
				String.format("Press A toggle %s AUTOPLAY mode%n",Gameplay.autoplay ? "remove" : "set") +
				String.format("Press H toggle %s generated highscore%n",Highscore.REC_NG ? "hide" : "show") +
				String.format("Press I,O toggle orientation (%2s,%s)%n",Integer.toBinaryString(Gameplay.orientation),
					Arrays.stream(Array.with(
						Gameplay.orientation == 0 ? "NM" : "",
						BitOperator.readBit(Gameplay.orientation,0) == 1 ? "MR" : "",
						BitOperator.readBit(Gameplay.orientation,1) == 2 ? "FL" : ""
					).toArray()).filter(x->x.length()>0).collect(Collectors.joining(","))
				) +
				String.format("Press R to refresh current data%n") +
				String.format("Press T toggle %s ASSIST TICK%n",Gameplay.assistTick ? "remove" : "set") +
				String.format("Press U toggle %s UNICODE%n",Metadata.unicode ? "remove" : "set") +
				ENDL +
				String.format("Chart Name:%n%s (from %s) [%s <%02d|Lv %02d>]%n%nHighscore:%n%s",
					chartGet().get1st().toStandardFormat(),
					chartGet().get1st().getSeries(),
					chartGet().get2nd().getDiffName(),
					chartGet().get2nd().getMode(),
					chartGet().get2nd().getDiffLevel(),
					Highscore.get().showPage(chartGet().get2nd(),hsPage,5,Highscore.REC_NG)
			),
			100, 128, 600, 1, false
		);
		for(AbstractInteract o : obj)
			o.draw(batch);

		batch.end();
	}
	public void processStepPost(float delta) {
	}
	// <<END>> Instance Structure
	// Constructors
	public ScreenMainMenu() {
		super(AdapterInputMainMenu.class);
		obj.add(new MainMenuInteractObjTest<>(gRef,200,200,new Rectangle(0,0,32,32)));
		Chartset.detect("resources\\Charts");
	}
	// Driver
}
