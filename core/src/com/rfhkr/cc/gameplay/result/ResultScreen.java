package com.rfhkr.cc.gameplay.result;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.*;
import com.rfhkr.cc.gameplay.*;
import com.sun.istack.internal.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/22
 */
public class ResultScreen extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static transient final ArrayMap<GameplayResult,ResultScreen> cache = new ArrayMap<>(32);
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	@NotNull
	public static ResultScreen show(
		@NotNull GameplayResult gRes
	) {
		System.out.println(gRes);
		ResultScreen sRef = null;
		if(cache.containsKey(gRes)) {
			// Move the cache to the back
			sRef = cache.get(gRes);
			cache.put(gRes,cache.removeKey(gRes));
		} else {
			// Check if the cache attempts to resize
			while(cache.size >= 32) {
				sRef = cache.getValueAt(0);
				sRef.dispose();
				cache.removeIndex(0);
			}
			cache.put(gRes,sRef = new ResultScreen(gRes));
		}
		System.out.println(sRef);
		System.out.println(cache);
		return sRef;
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	float timePassed = 0;
	public final GameplayResult shownResult;
	public final int chartRank;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	private float  bound(float  min,float  max,float  val) { return Math.max(min,Math.min(max,val)); }
	private double bound(double min,double max,double val) { return Math.max(min,Math.min(max,val)); }
	public void render(float delta) {
		// Clear the screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Update per frame
		super.render(delta);
		timePassed+=delta;
	}
	public void show() {}
	public void hide() {}
	public void pause() {}
	public void resume() {}
	public void resize (int width,int height) {}
	public void processStepPre (float delta) {}
	public void processStepMain(float delta) {}
	public void processStepDraw(float delta,SpriteBatch batch) {
		final Function<Integer,Integer>
			addNormAnim    = x -> (int)Math.round(x * bound(0,1,(timePassed - 0.25f) * 1.25)),
			addNextAnim    = x -> (int)Math.round(x * bound(0,1,(timePassed - 2.25f) * 0.75)),
			addLongAnim    = x -> (int)Math.round(x * bound(0,1,(timePassed - 0.25f) / 4f));
		final Function<Integer,Float>
			addExcelAnim   = x -> addNextAnim.apply(x)/10f;
		final Function<Map.Entry<?,Integer>,Integer>
			valueAnimation = x -> addNormAnim.apply(x.getValue()),
			addAnimation   = x -> addNextAnim.apply(x.getValue());

		batch.begin();

		// Draw Header
		gRef.font.getDefault().draw(batch,
			String.format("%s [%s] %n(Mode %02d) <Level %02d> by %s%nPlayed by %s at %s%n",
				shownResult.getChartset().getMetadata().toStandardFormat(),
				shownResult.getChart().getDiffName(),
				shownResult.getChart().getMode(),
				shownResult.getChart().getDiffLevel(),
				shownResult.getChart().getDiffCharter(),
				shownResult.getPlayer(),
				shownResult.getPlayTimeStr()
			),
			16,16
		);

		// Draw Captions Scores
		gRef.font.getDefault().draw(batch,
			String.format("Note Score:%n%n%s",
				shownResult.getScoreRef().keySet().stream()
					.map(Object::toString)
					.collect(Collectors.joining("\n"))
			),
			 16,96,304,-1,false
		);
		gRef.font.getDefault().draw(batch,
			String.format("Judgement:%n%n%s%n%n%s",
				shownResult.getJudgeRef().keySet().stream()
					.filter(x -> x != Judgement.JUST)
					.map(Object::toString)
					.collect(Collectors.joining("\n")),
				"COMBO"
			),
			336,96,304,-1,false
		);

		// Draw Score Values
		gRef.font.getDefault().draw(batch,
			String.format("%n%n%s",
				shownResult.getScoreRef().entrySet().stream()
					.map(valueAnimation)
					.map(Object::toString)
					.collect(Collectors.joining("\n"))
			),
			 16,96,192, 0,false
		);
		gRef.font.getDefault().draw(batch,
			String.format("%n%n%s%n%n%s",
				shownResult.getJudgeRef().entrySet().stream()
					.filter(x->x.getKey()!=Judgement.JUST)
					.map(valueAnimation)
					.map(Object::toString)
					.collect(Collectors.joining("\n")),
				addNormAnim.apply(shownResult.getMaximumCombo())
			),
			336,96,192, 0,false
		);

		// Draw Just Values
		final float
			flash = (float)Math.sin(timePassed * 49);
		final int
			justv = shownResult.getJudgeRef().get(Judgement.JUST);
		gRef.font.getDefault().setColor(1.0f,flash,flash,1.0f);
		gRef.font.getDefault().draw(batch,
			String.format("%n%n%s",
				shownResult.getJustRef().entrySet().stream()
					.map(addAnimation)
					.map(x -> x == 0 ? "" : String.format("%+d",x))
					.collect(Collectors.joining("\n"))
			),
			208,96,116,-1,false
		);
		if(justv>0)
			gRef.font.getDefault().draw(batch,
				String.format("%n%n%+1.1f",
					addExcelAnim.apply(justv)
				),
				528,96,116,-1,false
			);
		gRef.font.getDefault().setColor(1,1,1,1);

		// Draw Achievement Rate and Score
		gRef.font.getDefault().draw(batch,
			String.format("Achievement %6.2f%%%nScore %09d%n%n%s",
				addLongAnim.apply(Math.round(shownResult.getAchievement()*100))/100f,
				addLongAnim.apply(shownResult.getTotalScore()),
				(timePassed > 5) ? String.format(
					"%s%n%s%n%s%n%n%s",
					"Rank " + shownResult.getRank().rankStr,
					shownResult.getRank().passFlag ? "CLEAR" : "",
					chartRank > 0 ? ("New Highscore at #"+chartRank) : "",
					"Press ENTER to return main menu"
				) : ""
			),
			300,320,200,1,false
		);

		batch.end();
	}
	public void processStepPost(float delta) {}
	// <<END>> Instance Structure
	// Nested Classes
	// Constructors
	private ResultScreen(GameplayResult result) {
		super(AdapterInputResult.class);
		this.shownResult = result;
		this.chartRank   = Highscore.get().getR(shownResult.getChart(),shownResult,!shownResult.isRecordable());
	}
	// Driver
}
