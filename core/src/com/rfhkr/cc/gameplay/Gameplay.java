package com.rfhkr.cc.gameplay;

import static com.rfhkr.cc.CCMain.ENDL;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer;
import com.rfhkr.cc.*;
import com.rfhkr.cc.errors.*;
import com.rfhkr.cc.gameplay.result.*;
import com.rfhkr.cc.io.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.cc.level.Chart.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.io.*;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public class Gameplay extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static boolean assistTick = true;
	public static boolean autoplay = true;
	public static final Byte[] posBuff = {-1,-1,-1,-1};
	public static final Setup setup = new Setup();
	public static final Map<Integer,Array<Vector2>> posMap = new TreeMap<>();
	private static boolean  firstRun = true;
	private static long     calibratedBGM;
	private static long     calibrateBack;
	private static Timer    timer = new Timer();
	private static Gameplay self;
	private static Vector2  lastCachedSize = new Vector2();
	private static final boolean testMode = true;
	private static final Vector2 lastRecordedInput = new Vector2();
	// ** ACCESSORS
	/** retrieves the current gameplay object
	 * @return last assigned object
	 */
	public static Gameplay now() {
		Objects.requireNonNull(self, "Cannot retrieve null reference for singleton-ish class");
		return self;
	}
	/** replaces current gameplay object
	 * @param now new assigned gameplay screen
	 * @return new assigned gameplay screen
	 */
	private static Gameplay now(Gameplay now) {
		if (Objects.nonNull(self)) {
			self.dispose();
		}
		return (self = now);
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static Vector2 getCenterScreen() {
		Twin<Integer> s = now().gRef.getSize();
		return lastCachedSize.set(s.getFirst()/2,s.getSecond()/2);
	}
	private static Array<Vector2> assignPosition(int mode) {
		if(!posMap.containsKey(mode)) {
			Array<Vector2> mapping = new Array<>(mode);
			final double
				angleStart = 450,
				angleIncr  = 360.0/mode,
				angleCue = angleStart - angleIncr/2,
				radius = 240;
			final Vector2
				center = new Vector2(getCenterScreen());
			double angle;
			int keyIndex = 0;
			while(keyIndex < mode)
				mapping.add(
					(new Vector2(center))
						.add(
							(float)( radius * Math.cos(angle = Math.toRadians(angleCue - angleIncr * keyIndex++))),
							(float)(-radius * Math.sin(angle))
						)
				);
			posMap.put(mode,mapping);
		}
		return posMap.get(mode);
	}
	/**
	 * reads the last recorded input by game
	 */
	public static Vector2 input() { return lastRecordedInput; }
	private static Vector2 input(float x, float y) {
		/** checks whether the mouse is near to center */
		final Vector2
			cpos = new Vector2(x,y);
		return lastRecordedInput
						 .set((cpos.dst(getCenterScreen()) < 16.0f) ? lastRecordedInput : cpos);
	}
	private static float getInputAngle() {
		final float r = (360.0f/now().getMode());
		float res = Math.round(
			Math.toDegrees(Math.atan2(lastRecordedInput.x-getCenterScreen().x,getCenterScreen().y-lastRecordedInput.y))/r
			- 0.5f
		)*r;
		return res<0? res+360 : res;
	}
	public static int getMouseFieldPos() {
		final float r = (360.0f/now().getMode());
		return (int)(getInputAngle()/r) + 1;
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private long     timeLoss;
	private float    timeElapsed;
	private Music    bgm;
	private Array<Sound> sfx;
	private Array<Float>
		touchTest, // indicates touch check flag
		touchRls,  // indicates last release touch
		touchHit;  // indicates last  hover  touch
	private boolean[][]
		buffState; // affects touchRls and touchHit
	private Array<Judgement> lastJudge;
	private float    lastNoteTime;
	private Sound    noteTick;
	private int      combo;
	private Chartset selectedSet;
	private byte     chartIndex;
	private Timing   currentTiming;
	private Timingset currentTimings;
	private TextureRegion bg;
	private Array<TextureRegion> hitZone;
	private GameplayResult gameResult;
	// ** ACCESSORS
	public float    getElapsed() { return timeElapsed; }
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
	float getLastHit(int pos) {
		return touchHit.get(--pos);
	}
	float getLastRls(int pos) {
		return touchRls.get(--pos);
	}
	boolean getIsHit(int pos) {
		--pos; return touchHit.get(pos)>touchRls.get(pos);
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public void dispose() {
		super.dispose();
		//sfx.forEach(Sound::dispose);
		bg.getTexture().dispose();
		if(hitZone!=null) {
			hitZone.forEach(s -> s.getTexture().dispose());
			hitZone.clear();
		}
		bgm.dispose();
		noteTick.dispose();
		for(AbstractInteract o : obj)
			o.dispose();
		timer.clear();
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
		if(bgm.isPlaying())
			timeElapsed = bgm.getPosition();
		if(timeElapsed>=0) bgm.pause();
		timeLoss = System.nanoTime() / 1000000;
	}
	public void resume () {
		if(bgm.getPosition()>0 && timeElapsed>0) bgm.play();
		long delta = System.nanoTime() / 1000000 - timeLoss;
		System.out.printf("Out of focus for: \u001b[1;31m%4d\u001b[mms%n",delta);
		//timer.delay(-delta);
	}
	public void resize (int width,int height) {
	}
	// ** METHODS
	public void processStepPre (float delta) {
		if(delta > 0.1f && bgm.getPosition() < delta) {
			Gdx.app.log("LAG",String.format("Delay @%1.3fsec",delta));
			timer.delay(Math.round(delta*1000));
			Gdx.app.log("Timer",String.format("Added @%4dmsec",Math.round(delta * 1000)));
		}

		calibrateBack = calibratedBGM + Math.round(lastNoteTime * 1_000);
		if(bgm.isPlaying()) {
			long missRate;

			// calibrate from front
			missRate = calibratedBGM - (System.nanoTime() - Math.round((double)bgm.getPosition() * 1_000_000_000L));
			if(Math.abs(missRate) > 5_000_000L) {
				Gdx.app.log("TimerShift",String.format("BGM Miss %4dms",missRate / 1000000));
				calibratedBGM -= missRate;
				timer.delay(missRate / 1_000_000);
			}

			// calibrate from back
			calibrateBack = calibratedBGM + Math.round(lastNoteTime * 1_000_000_000D);
			missRate = (calibrateBack/1_000_000 - FinishHandle.self.getExecuteTimeMillis());
			if(Math.abs(missRate) > 5L) {
				Gdx.app.log("TimerShift",String.format("CRD Miss %4dms",missRate));
				timer.delay(missRate);
			}
		} else {
			calibratedBGM = System.nanoTime() + Math.round((double)bgm.getPosition() * 1_000_000_000L);
		}

		timeElapsed = (timeElapsed < 0) ? timeElapsed+delta : bgm.isPlaying() ? bgm.getPosition() : timeElapsed;
		currentTiming = selectedSet.getMetadata().getTimingSet().approx(timeElapsed,256);
		input(Gdx.input.getX(),Gdx.input.getY());
		for(int i=0;i<buffState.length;i++)
			Arrays.fill(buffState[i],false);
		for(int i=0;i<4 && posBuff[i]>0;i++)
			buffState[0][posBuff[i]-1] = true;
		CursorHandler.get(getMouseFieldPos(),posBuff);
		for(int i=0;i<4 && posBuff[i]>0;i++)
			buffState[1][posBuff[i]-1] = true;
		for(int s=0;s<getMode();s++)
			if(buffState[0][s]^buffState[1][s]) // check if altered state
				(/* checks whether from HIT to RLS or viceversa */(buffState[0][s]) ? touchRls : touchHit)
					.set(s,timeElapsed-delta);
		for(int i=0;i<4 && posBuff[i]>0;i++)
			if(buffState[1][posBuff[i]-1] && !getIsHit(posBuff[i]))
				touchHit.set(posBuff[i]-1,touchRls.get(posBuff[i]-1)+1e-3f);
			else if(!buffState[1][posBuff[i]-1] && getIsHit(posBuff[i]))
				touchRls.set(posBuff[i]-1,touchHit.get(posBuff[i]-1)+1e-3f);
	}
	public void processStepMain(float delta) {
		for(AbstractInteract o : obj)
			o.render(delta).update();
	}
	public void processStepDraw(float delta,SpriteBatch batch) {
		batch.begin();
		if(bg!=null)
			batch.draw(bg,0,0,gRef.getSize().get1st(),gRef.getSize().get2nd());
		if(hitZone!=null)
			for(int i=0;i<selectedSet.getDifficulties(chartIndex).getMode();i++) {
				boolean hovered = Arrays.asList(posBuff).indexOf((byte)(i+1))>=0;
				Color drawColor = batch.getColor();
				drawColor.set(1.0f,1.0f,1.0f,1.0f);
				drawColor.a = (1.0f - (timeElapsed > touchTest.get(i) ? 0.0f : 0.5f))/5f;
				switch(lastJudge.get(i)) {
					case JUST:
						drawColor.r *= 1.0;
						drawColor.g *= 0.3;
						drawColor.b *= 0.1;
						break;
					case EXCEL:
						drawColor.r *= 0.8;
						drawColor.g *= 0.5;
						drawColor.b *= 0.2;
						break;
					case HIT:
						drawColor.r *= 0.4;
						drawColor.g *= 0.8;
						drawColor.b *= 0.4;
						break;
					case BAD:
						drawColor.r *= 0.3;
						drawColor.g *= 0.3;
						drawColor.b *= 0.6;
						break;
				}
				if (hovered)
					drawColor.mul(1.0f,0.6f,1.0f,5.0f);
				batch.setColor(drawColor);
				batch.draw(hitZone.get(i),
					getCenterScreen().x,getCenterScreen().y - 256,
					0.0f,256.0f,
					256.0f,256.0f,
					-1,1,
					i * 360f / selectedSet.getDifficulties(chartIndex).getMode() + 90,true
				);
				batch.setColor(1.0f,1.0f,1.0f,1.0f);
			}
		gRef.font.getCurrent().draw(batch,
			String.format("%s - %s",getMetadata().getComposer(),getMetadata().getTitle()),
			32,32
		);
		gRef.font.getCurrent().draw(batch,
			String.format("(%s %02d) <%02d> by %s",
				selectedSet.getDifficulties(chartIndex).getDiffName(),
				selectedSet.getDifficulties(chartIndex).getDiffLevel(),
				selectedSet.getDifficulties(chartIndex).getMode(),
				selectedSet.getDifficulties(chartIndex).getDiffCharter()
			),
			32, 48
		);
		gRef.font.getCurrent().setColor(1.0f,1.0f * (bgm.isPlaying() ? 0 : 1),1.0f * (bgm.isPlaying() ? 0 : 1),1.0f);
		gRef.font.getCurrent().draw(batch,
			String.format("%6.3fsec%n%s%nPosition: %d (%4.2f,%4.2f,%4.2fdeg)",timeElapsed,currentTiming,
				getMouseFieldPos(),lastRecordedInput.x,lastRecordedInput.y,
				getInputAngle()
			),
			400,48,368, 0, false
		);
		if(autoplay)
			gRef.font.getCurrent().draw(batch,
				String.format("AUTOPLAY <Moves: %04d unit(s)>",
					AutoInputHandle.moveCount
				),
				400,104,368, 0, false
			);
		gRef.font.getCurrent().draw(batch,
			String.format("Score %09d%n%s%n%nPass Score %09d%nSS Score %09d%nAchievement %1.2f%% (Rank %s)%nCombo %d(%d)%n%nJudgement%n%s",
				gameResult.getTotalScore(),
				gameResult.getScoreRef().entrySet().stream().map(x ->
						String.format("%s: %4dpts",x.getKey(),x.getValue())
				).collect(Collectors.joining(ENDL)),
				Math.round(gameResult.getMaximumScore() * GameplayResult.Rank.BM.accReq / 100),
				gameResult.getMaximumScore(),
				gameResult.getAchievement(),
				gameResult.getRank().rankStr,
				combo,
				gameResult.getMaximumCombo(),
				gameResult.getJudgeRef().entrySet().stream().map(x ->
						String.format("%s x%4d",x.getKey(),x.getValue())
				).collect(Collectors.joining(ENDL))
			),
			400,128,368,0,false
		);
		gRef.font.getCurrent().setColor(1.0f,1.0f,1.0f,1.0f);
		gRef.font.getCurrent().draw(batch,
			String.format("[%d (%d-%d-%d) %d]",
				calibratedBGM/1000000,
				TimeUtils.nanosToMillis(TimeUtils.nanoTime()),
				Math.abs(calibrateBack - calibratedBGM)/1000000,
				calibrateBack/1000000,
				FinishHandle.self.getExecuteTimeMillis()
			),
			720,560,80,0,false
		);
		gRef.font.getCurrent().draw(batch,
			String.format("%06.2f (%03d)fps (%07.3f%%)",
				1 / delta,
				Gdx.graphics.getFramesPerSecond(),
				Gdx.graphics.getFramesPerSecond()*delta*100
			),
			720,576,80,0,false
		);
		//gRef.font.getDefault().draw(batch, "Circle Clocker", 100, 64, 600, 1, false);
		for(AbstractInteract o : obj)
			o.draw(batch);

		batch.end();
	}
	public void processStepPost(float delta) {
	}
	void applyJudge(int i,Judgement res,boolean colorChange) {
		if(!colorChange || res.ordinal() < lastJudge.get(i).ordinal())
			lastJudge.set(i,res);
		if(res != Judgement.JUST)
			combo = res.keepCombo ? ++combo : 0;
		gameResult.addCounter(res);
		gameResult.setMaxCombo(Math.max(gameResult.getMaximumCombo(),combo));
	}
	void onJudgeGiven(@NotNull NoteBasic note,boolean[] justFlag){
		Judgement j = autoplay?Judgement.EXCEL:note.getWorstJudgement();
		applyJudge(note.getNotePos()-1,j,justFlag[0]||justFlag[1]);
		gameResult.addCounter(note.getType(),Math.round(j.baseScore * note.getType().scoreMult));
		obj.removeValue(note,false);
		note.hasJudged();
	}
	void onJustTiming(@NotNull NoteBasic note) {
		applyJudge(note.getNotePos()-1,Judgement.JUST,true);
		gameResult.addJBonus(note.getType());
	}
	private void determineBestRoute(final Supplier<Float> calibrator) {
		CursorHandler.ChartSolver.clear();
		Chart     chartRef  = selectedSet.getDifficulties(chartIndex);
		Timing    lastTime  = Timing.shift(chartRef.chart.peek().getEnd(),Timing.at(1));
		System.out.printf("%s @%1.3fsec%n",lastTime,currentTimings.at(lastTime));
		Note      lastNote  = new Note(NoteType.NOTE_NORM,1,lastTime,lastTime);
		float[]   statetime = new float[getMode()];
		boolean[] statenow, statenext;
			statenow  = new boolean[(statenext = new boolean[getMode()]).length];
		float timeprev = -0.001f;
		Arrays.fill(statetime,-0.001f);
		Arrays.fill(statenow ,false);
		Arrays.fill(statenext,false);
		chartRef.chart.add(lastNote);
		for(Note note : chartRef.chart) {
			// Let previous time initialized and not match with current time to process the next sequence
			float timenow = currentTimings.at(note.getStart());
			if (timeprev != timenow) {
				// refresh state
				CursorHandler.ChartSolver.send(timeprev,Arrays.copyOf(statenext,statenext.length));
				statenow = Arrays.copyOf(statenext,statenext.length);
				timeprev = timenow;
				// release all the state that already expires
				for(int i=0;i<statenext.length;i++)
					statenext[i] &= statetime[i] >= timeprev;
				for(int i=0;i<statenow.length;i++)
					statenow[i] &= statetime[i] > timenow;
			}
			statenext[(byte)(note.getPos()-1)] = true;
			statetime[(byte)(note.getPos()-1)] = currentTimings.at(note.getEnd());
		}
		CursorHandler.ChartSolver.send(timeprev,Arrays.copyOf(statenext,statenext.length));
		chartRef.chart.removeValue(lastNote,true);
		CursorHandler.ChartSolver.solve();
		CursorHandler.ChartSolver.getSolution().forEach((time,type) ->
			timer.scheduleTask(new AutoInputHandle(type.getX(),type.getY()),time + calibrator.get())
		);
				//System.out.printf("%1.3f -> calibrated %+1.3f%n",time,calibrator.get());
	}
	// <<END>> Instance Structure
	// Nested Classes
	public static final class Setup /* Struct */ {
		// Class Properties
		public final double GSMX = 10;
		public final double GSSN = 13.3;
		private final double powerRate = Math.log(Math.pow(4,0.25));
		private int approach = 4;
		private boolean plus = false;
		// Class Accessors
		public float approach() { return approach(approach,plus); }
		public float approach(int GS) { return approach(GS,plus); }
		public float approach(int GS, boolean GSP) {
			return (approach=GS) + ((plus=GSP) ? 1 : 0) / 2.0f;
		}
		public void  approachSucc() {
			if (!isSpeedG2GF()) approach(approach + (!plus ? 0 : 1),!plus);
		}
		public void  approachPred() {
			if (!isSpeedSlug()) approach(approach - (plus ? 0 : 1),!plus);
		}
		public boolean isSpeedSlug() { return approach() <= 1 && (approach(1) == 1); }
		public boolean isSpeedG2GF() { return approach() > GSMX; }
		public float getApproachTime() {
			return getApproachTime(isSpeedG2GF() ? GSSN : approach());
		}
		private float getApproachTime(double approach) { return (float)Math.exp((5 - approach)*powerRate); }
	}
	private static final class SingletonCalibrator extends Timer.Task {
		public static SingletonCalibrator self = new SingletonCalibrator();
		public void run() {}
	}
	private static final class BGMCalibrator extends Timer.Task {
		public static final BGMCalibrator self = new BGMCalibrator();
		public void run() {
			Gameplay.now().bgm.play();
		}
	}
	private static final class AssistTick extends Timer.Task {
		public static AssistTick self = new AssistTick();
		public static AssistTick last = null;
		public void run() {
			if(now().bgm.isPlaying()||now().timeElapsed<0)
				now().noteTick.play();
			else
				cancel();
		}
		/** Bald Constructor */
		{
			last = this;
		}
	}
	private static final class TouchCheck extends Timer.Task {
		private int id;
		private float end;
		public void run() {
			now().touchTest.set(id,now().timeElapsed + ((end<=0.0f) ? 0.0f : end) + 0.1f);
			//now().applyJudge(id,Judgement.values()[(int)Math.floor(Math.random() * Judgement.values().length)]);
			//System.out.println(now().touchTest);
		}
		public TouchCheck(int id) { this(id,0.0f); }
		public TouchCheck(int id,float end) {
			super();
			this.id = id;
			this.end = end;
		}
	}
	private static final class NoteCreation extends Timer.Task {
		private static float approach = setup.getApproachTime();
		private NoteBasic item;
		public void run() {
			if(Objects.nonNull(item))
				Gameplay.now().obj.add(item);
		}
		public NoteCreation(@NotNull Note noteData) {
			switch(noteData.getType()) {
				case NOTE_NORM:
					item = new NoteSingle(noteData.getPos(),
						now().currentTimings.at(noteData.getStart())
					);
					break;
				case NOTE_LONG:
					item = new NoteLong(noteData.getPos(),
						now().currentTimings.at(noteData.getStart()),
						now().currentTimings.at(noteData.getEnd())
					);
					break;
				case NOTE_SLDE:
					break;
				default:
					break;
			}
		}
	}
	private static final class AutoInputHandle extends Timer.Task {
		private static final Pair<CursorHandler.Mode,Byte> lastCommand = Pair.gen(null,null);
		private final Pair<CursorHandler.Mode,Byte> cmd;
		public static int moveCount = 0;
		public void run() {
			int
				x = (int)posMap.get(Gameplay.now().getMode()).get(cmd.get2nd()-1).x,
				y = (int)posMap.get(Gameplay.now().getMode()).get(cmd.get2nd()-1).y;
			Gdx.input.setCursorPosition(x,y);
			CursorHandler.type(cmd.get1st());
			if(cmd.get1st() == CursorHandler.Mode.SINGLE)
				return;
			if(lastCommand.get1st()!=null)
				moveCount += CursorHandler.ChartSolver.moveCount(cmd,lastCommand);
			lastCommand.setBoth(cmd.get1st(),cmd.get2nd());
		}
		public AutoInputHandle(CursorHandler.Mode m,Byte p) {
			cmd = Pair.gen(m,p);
		}
		public AutoInputHandle(Pair<CursorHandler.Mode,Byte> p) {
			cmd = p;
		}
	}
	private static final class FinishHandle extends Timer.Task {
		private static final FinishHandle self = new FinishHandle();
		public void run() {
			if(!autoplay)
				Highscore.get().getScores(now().getChart()).add(now().gameResult);
			now().bgm.stop();
			now().requestNewScreen(ResultScreen.show(now().gameResult));
			Highscore.get().saveScores(now().gameResult.getChart());
		}
	}
	// Constructors
	public Gameplay(Chart selectedChart) {
		super(AdapterInputGameplay.class);
		now(this);
		AutoInputHandle.moveCount = 0;
		FileFormatReader<?> pars;
		PathResolver        nova;
		Path                arpg;
		selectedSet = Chartset.find(selectedChart);
		chartIndex  = (byte)selectedSet.getDifficulties().indexOf(selectedChart,true);
		assignPosition(selectedSet.getDifficulties(chartIndex).getMode());
		currentTimings = selectedSet.getMetadata().getTimingSet();
		combo = 0;
		timeElapsed = 0;
		gameResult = GameplayResult.make(selectedSet.getDifficulties(chartIndex));
		gameResult.setPlayTime(Instant.now())
			.setRecordable(!autoplay)
			.setPlayer("GUEST CLOCKER")
			.setSpeed(setup.approach());
		arpg = (new File(selectedSet.getPath().resolve())).toPath().toAbsolutePath();
		System.out.println(arpg + "\\" + selectedSet.getSongBG());
		try {
			bg = new TextureRegion(new Texture(arpg + "\\" + selectedSet.getSongBG()));
			bg.flip(false,true);
		} catch (Exception e) { bg = null; }
		hitZone = new Array<>(0);
		try {
			int i = 0, j = selectedSet.getDifficulties(chartIndex).getMode();
			hitZone = new Array<>(false,j);
			touchRls = new Array<>(touchHit = new Array<>(touchTest = new Array<>(false,j)));
			lastJudge = new Array<>(false,j);
			buffState = new boolean[2][getMode()];
			while(i++ < j) {
				hitZone.add(
					new TextureRegion(
						new Texture(String.format("core\\assets\\pie%02d.png",j))
					)
				);
				touchTest.add(0.0f);
				touchHit .add(-0.001f);
				touchRls .add(-0.001f);
				lastJudge.add(Judgement.MISS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			hitZone.forEach(s -> s.getTexture().dispose());
			hitZone = null;
		}
		bgm = Gdx.audio.newMusic(Gdx.files.internal(arpg + "\\" + selectedSet.getSongName()));
		bgm.setVolume(0.2f);
		noteTick = Gdx.audio.newSound(Gdx.files.internal(PathResolver.at("tick.ogg").build("core","assets").toString()));
//		timer.scheduleTask(AssistTick.self,
//			currentTimings.getFirstOffset(),
//			(float)(60.0/currentTimings.earliest().getBPM())
//		);
		/** no offset needed, as long as it was done on this anonymous class **/
		float
			earlyStart = Timing.interval(currentTimings.approx(0),Timing.at(4)).toFloat(currentTimings.earliest()),
			earlyNote  = currentTimings.at(selectedSet.getDifficulties(chartIndex).chart.get(0).getStart())
				- setup.getApproachTime();
		boolean
			isNoteFirst = Float.compare(earlyStart,earlyNote)>0;
		//System.out.println(earlyStart);
		//System.out.println(earlyNote);
		//System.out.println(isNoteFirst);
		CursorHandler.type(CursorHandler.Mode.SPREAD);
		CursorHandler.get(1,posBuff);
		NoteBasic.approach = setup.getApproachTime();
		calibratedBGM = System.nanoTime();
		timer.postTask(new Timer.Task() {
			public void run() {
				timeElapsed = Math.min(0,Math.min(earlyNote,earlyStart));
				long timerOffset = timer.postTask(SingletonCalibrator.self).getExecuteTimeMillis();
				long solveTime = System.nanoTime()/1_000_000;
				final Supplier<Float> calibrator = ()->
					(solveTime - System.nanoTime()/1_000_000)/1e+3f;
				if (autoplay) {
					timer.postTask(new AutoInputHandle(CursorHandler.Mode.SINGLE,(byte) 1));
					determineBestRoute(calibrator);
				}
				timer.scheduleTask(new AssistTick(),
					timeElapsed - Timing.at(12).toFloat(currentTimings.earliest()),
					(float)(60.0/currentTimings.earliest().getBPM())+calibrator.get(), 4
				);
				selectedChart.chart.forEach(note -> {
					timer.postTask(new NoteCreation(note));
					if(autoplay)
						timer.scheduleTask(
							note.getType()==NoteType.NOTE_NORM ?
								new TouchCheck(note.getPos()-1) :
								new TouchCheck(note.getPos()-1,note.getLength().toFloat(currentTimings.bpm(note.getStart()))),
							currentTimings.at(note.getStart())+calibrator.get()
						);
					if(autoplay||assistTick)
						timer.scheduleTask(new AssistTick(),
							currentTimings.at(note.getStart())+calibrator.get(),
							note.getLength().toFloat(currentTimings.bpm(note.getStart())),
							note.getType()==NoteType.NOTE_NORM ? 0 : 1
						);
				});
				timer.scheduleTask(FinishHandle.self,
					lastNoteTime = currentTimings.at(selectedChart.chart.peek().getEnd()) + 4.0f
				);
				timer.delay(Math.round(-timeElapsed * 1000));
				if(firstRun)
					timer.delay(70);
				firstRun&=false;
				timer.scheduleTask(BGMCalibrator.self,-timeElapsed+calibrator.get());
				//timer.delay((timerOffset - solveTime) * 2);
				//timer.delay(solveTime - System.nanoTime() / 1000000);
			}
		});
		//timer.delay(-1000);
	}
	// Driver
}
