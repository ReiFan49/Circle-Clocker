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
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
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
		if(timeElapsed>=0) bgm.play();
		long delta = System.nanoTime() / 1000000 - timeLoss;
		System.out.printf("Out of focus for: \u001b[1;31m%4d\u001b[mms%n",delta);
		timer.delay(-delta);
	}
	public void resize (int width,int height) {
	}
	// ** METHODS
	public void processStepPre (float delta) {
		timeElapsed = (timeElapsed < 0) ? timeElapsed+delta : bgm.isPlaying() ? bgm.getPosition() : timeElapsed;
		currentTiming = selectedSet.getMetadata().getTimingSet().approx(timeElapsed,256);
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
				batch.setColor(1.0f,1.0f,1.0f,
					1.0f - (timeElapsed > touchTest.get(i) ? 0.0f : 0.5f));
				batch.draw(hitZone.get(i),
					getCenterScreen().x,getCenterScreen().y-256,
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
		gRef.font.getDefault().draw(batch,
			String.format("%6.3fsec%n%s",timeElapsed,currentTiming),
			400, 48, 368, 0, false
		);
		gRef.font.getDefault().draw(batch,
			String.format("%6.1f (%3d)fps",1/delta,Gdx.graphics.getFramesPerSecond()),
			720, 576, 80, 0, false
		);
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
		public int approach = 4;
		private final double powerRate = Math.log(Math.pow(3,0.25));
		// Class Accessors
		public float getApproachTime() {
			return (float)Math.exp((5 - approach)*powerRate);
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
		public void run() {}
		public NoteCreation(@NotNull Note noteData) {
			switch(noteData.getType()) {
				case NOTE_NORM:
					item = new NoteSingle(noteData.getPos(),
						now().currentTimings.at(noteData.getStart())
					);
					break;
				case NOTE_LONG:
					break;
				case NOTE_SLDE:
					break;
				default:
					break;
			}
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
			hitZone = new Array<>(j);
			touchTest = new Array<>(new Float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f});
			while(i++ < j)
				hitZone.add(
					new TextureRegion(
						new Texture(String.format("core\\assets\\pie%02d.png",j))
					)
				);
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
			earlyNote  = currentTimings.at(selectedSet.getDifficulties(chartIndex).chart.get(0).getStart());
		boolean
			isNoteFirst = Float.compare(earlyStart,earlyNote -= setup.getApproachTime())>0;
		timer.scheduleTask(new AssistTick(),
			earlyStart - timeElapsed + Timing.at(12).toFloat(currentTimings.earliest()),
			(float)(60.0/currentTimings.earliest().getBPM()), 3
		);
		System.out.println(timer.scheduleTask(new Timer.Task() {
			public void run() {
				bgm.play();
				selectedSet.getDifficulties(chartIndex).chart.forEach(note -> {
					timer.scheduleTask(
						new NoteCreation(note),
						currentTimings.at(note.getStart()) - setup.getApproachTime()
					);
					timer.scheduleTask(new AssistTick(),currentTimings.at(note.getStart()));
					timer.scheduleTask(
						(note.getType() != NoteType.NOTE_NORM) ?
						new TouchCheck(note.getPos() - 1,
							note.getLength().toFloat(currentTimings.bpm(note.getStart()))) :
						new TouchCheck(note.getPos() - 1),
						currentTimings.at(note.getStart())
					);
				});
			}
		},-(timeElapsed = Math.min(0.0f,Math.min(earlyStart,earlyNote)))));
	}
	// Driver
}
