package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer;
import com.rfhkr.cc.*;
import com.rfhkr.cc.errors.*;
import com.rfhkr.cc.io.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.cc.level.Chart.*;
import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/08
 */
public class Gameplay extends AbstractScreen {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static Timer    timer = new Timer();
	public static boolean autoplay = true;
	private static Gameplay self;
	private static Vector2 lastCachedSize = new Vector2();
	private static final boolean testMode = true;
	private static final Vector2 lastRecordedInput = new Vector2();
	public static final Setup setup = new Setup();
	public static final Map<Integer,Array<Vector2>> posMap = new TreeMap<>();
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
	private long     tiMillis;
	private long     timeLoss;
	private float    timeElapsed;
	private Path     arpg;
	private Music    bgm;
	private Array<Sound> sfx;
	private Array<Float> touchTest;
	private Sound    noteTick;
	private int      combo;
	private Chartset selectedSet;
	private byte     chartIndex;
	private Timing   currentTiming;
	private Timingset currentTimings;
	private TextureRegion bg;
	private Array<TextureRegion> hitZone;
	private Map<NoteType,Integer> score;
	private Map<Judgement,Integer> judge;
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
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public void dispose() {
		super.dispose();
		sfx.forEach(Sound::dispose);
		bg.getTexture().dispose();
		if(hitZone!=null) {
			hitZone.forEach(s -> s.getTexture().dispose());
			hitZone.clear();
		}
		bgm.dispose();
		noteTick.dispose();
		for(AbstractInteract o : obj)
			o.dispose();
		now(null);
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
		if(timeElapsed>=0) bgm.pause();
		timeLoss = System.nanoTime() / 1000000;
	}
	public void resume () {
		if(timeElapsed==bgm.getPosition()) bgm.play();
		long delta = System.nanoTime() / 1000000 - timeLoss;
		System.out.printf("Out of focus for: \u001b[1;31m%4d\u001b[mms%n",delta);
		timer.delay(-delta);
	}
	public void resize (int width,int height) {
	}
	// ** METHODS
	public void processStepPre (float delta) {
		if(delta > 0.067f) {
			Gdx.app.log("LAG",String.format("Delay @%1.3fmsec",delta));
			timer.delay(Math.round(delta*1000));
			Gdx.app.log("Timer",String.format("Added @%4dsec",Math.round(delta * 1000)));
		}
		tiMillis = System.currentTimeMillis();
		timeElapsed = (timeElapsed < 0) ? timeElapsed+delta : bgm.isPlaying() ? bgm.getPosition() : timeElapsed;
		currentTiming = selectedSet.getMetadata().getTimingSet().approx(timeElapsed,256);
		input(Gdx.input.getX(),Gdx.input.getY());
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
				Byte[] buff = new Byte[]{-1,-1,-1,-1};
				CursorHandler.get(getMouseFieldPos(),buff);
				boolean hovered = Arrays.asList(buff).indexOf((byte)(i+1))>=0;
				batch.setColor(1.0f,1.0f - (hovered ? 0.4f : 0.0f),1.0f,
					1.0f - (timeElapsed > touchTest.get(i) ? 0.0f : 0.5f));
				batch.draw(hitZone.get(i),
					getCenterScreen().x,getCenterScreen().y - 256,
					0.0f,256.0f,
					256.0f,256.0f,
					-1,1,
					i * 360 / selectedSet.getDifficulties(chartIndex).getMode() + 90,true
				);
				batch.setColor(1.0f,1.0f,1.0f,1.0f);
			}
		gRef.font.getDefault().draw(batch,
			String.format("%s - %s",getMetadata().getComposer(),getMetadata().getTitle()),
			32,32
		);
		gRef.font.getDefault().draw(batch,
			String.format("(%s %02d) <%02d> by %s",
				selectedSet.getDifficulties(chartIndex).getDiffName(),
				selectedSet.getDifficulties(chartIndex).getDiffLevel(),
				selectedSet.getDifficulties(chartIndex).getMode(),
				selectedSet.getDifficulties(chartIndex).getDiffCharter()
			),
			32, 48
		);
		gRef.font.getDefault().setColor(1.0f,1.0f * (bgm.isPlaying() ? 0 : 1),1.0f * (bgm.isPlaying() ? 0 : 1),1.0f);
		gRef.font.getDefault().draw(batch,
			String.format("%6.3fsec%n%s%nPosition: %d (%4.2f,%4.2f,%4.2fdeg)",timeElapsed,currentTiming,
				getMouseFieldPos(),lastRecordedInput.x,lastRecordedInput.y,
				getInputAngle()
			),
			400,48,368, 0, false
		);
		gRef.font.getDefault().setColor(1.0f,1.0f,1.0f,1.0f);
		gRef.font.getDefault().draw(batch,
			String.format("%6.1f (%3d)fps (%5dmsec)",
				1/delta
				,Gdx.graphics.getFramesPerSecond(),
				System.currentTimeMillis()-tiMillis
			),
			720, 576, 80, 0, false
		);
		//gRef.font.getDefault().draw(batch, "Circle Clocker", 100, 64, 600, 1, false);
		for(AbstractInteract o : obj)
			o.draw(batch);

		batch.end();
	}
	public void processStepPost(float delta) {
	}
	private void determineBestRoute() {
		CursorHandler.ChartSolver.clear();
		Chart     chartRef  = selectedSet.getDifficulties(chartIndex);
		Timing    lastTime  = Timing.shift(chartRef.chart.peek().getEnd(),Timing.at(1));
		Note      lastNote  = new Note(NoteType.NOTE_NORM,1,lastTime,lastTime);
		float[]   statetime = new float[getMode()];
		boolean[] statenow, statenext;
			statenow  = new boolean[(statenext = new boolean[getMode()]).length];
		float timeprev = -0.001f;
		Array<Pair<CursorHandler.Mode,Byte>> solution;
		Arrays.fill(statetime,-0.001f);
		Arrays.fill(statenow ,false);
		Arrays.fill(statenext,false);
		chartRef.chart.add(lastNote);
		for(Note note : chartRef.chart) {
			// Let previous time initialized and not match with current time to process the next sequence
			float timenow = currentTimings.at(note.getStart());
			if (timeprev != timenow) {
				if(timeprev >= 0.0f) {
					// DO SOMETHING
				}
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
		chartRef.chart.removeValue(lastNote,true);
		CursorHandler.ChartSolver.solve();
		CursorHandler.ChartSolver.getSolution().forEach((time,type) ->
			timer.scheduleTask(new AutoInputHandle(type.getX(),type.getY()),time)
		);
	}
	// <<END>> Instance Structure
	// Nested Classes
	public static final class Setup /* Struct */ {
		// Class Properties
		public int approach = 4;
		public boolean plus = false;
		private final double powerRate = Math.log(Math.pow(4,0.25));
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
		public boolean isSpeedG2GF() { return approach() > 10; }
		public float getApproachTime() {
			return isSpeedG2GF() ? 0.04f : (float)Math.exp((5 - approach())*powerRate);
		}
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
		public void run() {
			if(now().bgm.isPlaying()||now().timeElapsed<0)
				now().noteTick.play();
			else
				cancel();
		}
	}
	private static final class TouchCheck extends Timer.Task {
		private int id;
		private float end;
		public void run() {
			now().touchTest.set(id,now().timeElapsed + ((end<=0.0f) ? 0.0f : end) + 0.1f);
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
		private final Pair<CursorHandler.Mode,Byte> cmd;
		public void run() {
			int
				x = (int)posMap.get(Gameplay.now().getMode()).get(cmd.get2nd()-1).x,
				y = (int)posMap.get(Gameplay.now().getMode()).get(cmd.get2nd()-1).y;
			Gdx.input.setCursorPosition(x,y);
			CursorHandler.type(cmd.get1st());
		}
		public AutoInputHandle(CursorHandler.Mode m,Byte p) {
			cmd = Pair.gen(m,p);
		}
		public AutoInputHandle(Pair<CursorHandler.Mode,Byte> p) {
			cmd = p;
		}
	}
	// Constructors
	public Gameplay(final CCMain gRef, Class<? extends InputProcessor> inputClass) {
		super(gRef,inputClass);
		now(this);
		FileFormatReader<?> pars;
		PathResolver        nova;
		if(testMode) {
			pars = new OsuFileReader();
			nova = PathResolver.at("Hiro - VERTeX (Rei Hakurei) [Sample 08].osu")
				.build("resources","Charts","Hiro (maimai) - VERTeX");
			pars.parse(nova);
			selectedSet = Chartset.cache.iterator().next();
			chartIndex  = 0;
		}
		assignPosition(selectedSet.getDifficulties(chartIndex).getMode());
		currentTimings = selectedSet.getMetadata().getTimingSet();
		combo = 0;
		timeElapsed = 0;
		score = new TreeMap<>();
		judge = new TreeMap<>();
		arpg = (new File(nova.resolve())).toPath().getParent().toAbsolutePath();
		try {
			bg = new TextureRegion(new Texture(arpg + "\\" + selectedSet.getSongBG()));
			bg.flip(false,true);
		} catch (Exception e) { bg = null; }
		hitZone = new Array<>(0);
		try {
			int i = 0, j = selectedSet.getDifficulties(chartIndex).getMode();
			hitZone = new Array<>(false,j);
			touchTest = new Array<>(false,j);
			while(i++ < j) {
				hitZone.add(
					new TextureRegion(
						new Texture(String.format("core\\assets\\pie%02d.png",j))
					)
				);
				touchTest.add(0.0f);
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
		System.out.println(earlyStart);
		System.out.println(earlyNote);
		System.out.println(isNoteFirst);
		CursorHandler.type(CursorHandler.Mode.SPREAD);
		NoteBasic.approach = setup.getApproachTime();
		timer.postTask(new Timer.Task() {
			public void run() {
				timeElapsed = Math.min(0,Math.min(earlyNote,earlyStart));
				long timerOffset = timer.postTask(SingletonCalibrator.self).getExecuteTimeMillis();
				long solveTime = System.nanoTime()/1000000;
				if (autoplay) {
					timer.postTask(new AutoInputHandle(CursorHandler.Mode.SINGLE,(byte) 1));
					determineBestRoute();
				}
				timer.scheduleTask(new AssistTick(),
					timeElapsed - Timing.at(8).toFloat(currentTimings.earliest()),
					(float)(60.0/currentTimings.earliest().getBPM()), 3
				);
				selectedSet.getDifficulties(chartIndex).chart.forEach(note -> {
					timer.postTask(new NoteCreation(note));
					timer.scheduleTask(
						note.getType()==NoteType.NOTE_NORM ?
							new TouchCheck(note.getPos()-1) :
							new TouchCheck(note.getPos()-1,note.getLength().toFloat(currentTimings.bpm(note.getStart()))),
						currentTimings.at(note.getStart())
					);
					timer.scheduleTask(new AssistTick(),
						currentTimings.at(note.getStart()),
						note.getLength().toFloat(currentTimings.bpm(note.getStart())),
						note.getType()==NoteType.NOTE_NORM ? 0 : 1
					);
				});
				timer.delay(Math.round(-timeElapsed * 1000));
				timer.scheduleTask(BGMCalibrator.self,-timeElapsed);
				timer.delay((timerOffset - solveTime) * 2);
				timer.delay(solveTime - System.nanoTime() / 1000000);
			}
		});
		//timer.delay(-1000);
	}
	// Driver
}
