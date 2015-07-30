package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.math.*;
import com.rfhkr.cc.level.*;
import com.sun.istack.internal.*;
import static com.rfhkr.cc.level.Chart.NoteType;
import static com.rfhkr.cc.gameplay.Judgement.*;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/16
 */
public final class GameplayResult implements Comparable<GameplayResult>, Serializable {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static final transient DateTimeFormatter timeFormatter = DateTimeFormatter
		.ofLocalizedDateTime( FormatStyle.FULL )
		.withLocale( Locale.getDefault() )
		.withZone( ZoneId.systemDefault() );
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	public static int     compare(GameplayResult g1,GameplayResult g2) {
		if(!g1.playedChart.equals(g2.playedChart))
			throw new IllegalStateException("Cannot compare results between different charts.");
		int[] cp = {
			Integer.compare(g2.getTotalScore(),g1.getTotalScore()),
			Float  .compare(g2.getAchievement(),g1.getAchievement()),
			Integer.compare(g2.getMaximumCombo(),g1.getMaximumCombo()),
			Float.compare(g2.GS,g1.GS),
			Long.compareUnsigned(
				g1.getPlayTime().toEpochMilli(),
				g2.getPlayTime().toEpochMilli()
			),
			Integer.compare(g2.judge.get(JUST),g1.judge.get(JUST)),
			Integer.compare(g2.judge.get(EXCEL),g1.judge.get(EXCEL)),
			Integer.compare(g1.judge.get(HIT ),g2.judge.get(HIT )),
			Integer.compare(g1.judge.get(BAD ),g2.judge.get(BAD )),
			Integer.compare(g1.judge.get(MISS),g2.judge.get(MISS)),
		};
		int cv, ci = 0;
		do {
			cv = cp[ci++];
		} while (cv == 0 && ci < cp.length);
		return cv;
	}
	// ** METHODS
	public static GameplayResult make(Chart diff) {
		return new GameplayResult(diff);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private boolean  REC;
	private float    GS;
	private String   playerName;
	private Instant  playedTime;
	@NotNull
	private transient Chartset playedSet;
	@NotNull
	private transient Chart playedChart;
	private EnumMap<NoteType ,Integer> score;
	private EnumMap<NoteType ,Integer> just ;
	private EnumMap<Judgement,Integer> judge;
	private int   maximumScore;
	private int   maximumCombo;
	// ** ACCESSORS
	public String   getPlayer  () { return !REC ? "AUTO CLOCK" : playerName; }
	public Instant  getPlayTime() { return playedTime; }
	public float    getSpeed   () { return GS; }
	public String   getSpeedStr() {
		if((GS-0.25f)>Gameplay.setup.GSMX)
			return "SN";
		else
			return String.format("%d%s",(int)Math.floor(GS),((GS % 1) != 0) ? "P":"");
	}
	public Chartset getChartset() { return playedSet; }
	public Chart    getChart   () { return playedChart; }
	public EnumMap<NoteType ,Integer> getScoreRef() { return score; }
	public EnumMap<NoteType ,Integer> getJustRef () { return just ; }
	public EnumMap<Judgement,Integer> getJudgeRef() { return judge; }
	public int      getMaximumScore() { return maximumScore; }
	public int      getMaximumCombo() { return maximumCombo; }
	public GameplayResult setPlayer(String name) { playerName = name; return this; }
	public GameplayResult setSpeed(float NGS) { GS=Math.max(1,Math.min((float)Gameplay.setup.GSSN,NGS)); return this; }
	public GameplayResult setRecordable(boolean rec) { REC=rec; return this; }
	public GameplayResult setPlayTime(Instant time) {
		playedTime = time; return this;
	}
	public GameplayResult setChart   (Chart newDiff) {
		playedChart = newDiff;
		maximumScore = Arrays.stream(newDiff.chart.toArray()).mapToInt(x->
			(int)(EXCEL.baseScore * x.getType().scoreMult)
		).reduce(0,Math::addExact);
		return determineNewSet();
	}
	public GameplayResult setJustRef (EnumMap<NoteType ,Integer> newRef) { just =newRef; return this; }
	public GameplayResult setScoreRef(EnumMap<NoteType ,Integer> newRef) { score=newRef; return this; }
	public GameplayResult setJudgeRef(EnumMap<Judgement,Integer> newRef) { judge=newRef; return this; }
	public GameplayResult setMaxCombo(int newCombo) { maximumCombo = newCombo; return this; }
	// ** PREDICATES
	public boolean  isFullCombo() { return playedChart.chart.size == maximumCombo; }
	public boolean  isRecordable() {
		return REC;
	}
	// ** INTERACTIONS
	public int      compareTo(@NotNull GameplayResult other) {
		return compare(this,other);
	}
	// ** METHODS
	public void     addJBonus (NoteType  e) { just.put(e,just.getOrDefault(e,0)+50); }
	public void     addCounter(NoteType  e,int value) { score.put(e,score.getOrDefault(e,0)+value); }
	public void     addCounter(Judgement e) { judge.put(e,judge.getOrDefault(e,0)+1); }
	public String   getPlayTimeStr()  {
		return timeFormatter.format(playedTime);
	}
	public int      getTotalScore()   {
		return
			Stream.concat(score.entrySet().stream(),just.entrySet().stream())
			.mapToInt(Map.Entry::getValue).sum();
	}
	public float    getAchievement () { return 1e2f * getTotalScore() / getMaximumScore(); }
	public Rank     getRank() {
		if(playedChart == null)
			return Rank.E;
		if(false)
			System.out.printf("Maximum \u001b[1;33mJUST\u001b[m: \u001b[1;31m%d\u001b[m%n",
				Stream.of(playedChart.chart.toArray()).mapToInt(y->y.getType().ordinal()).sum()
			);
		return Stream.of(Rank.values()).filter(x->
			getAchievement() >= x.accReq &&
			judge.getOrDefault(JUST,0) >= Math.round(
				x.judgementRequired.getOrDefault(JUST,0f)*
				Stream.of(playedChart.chart.toArray()).mapToInt(y->y.getType().ordinal()).sum()
			)
		).findFirst().orElse(Rank.E);
	}
	public void     genMinimum() {
		setRecordable(false);
		setMaxCombo(0);
		setSpeed(4);
		Stream.of(just,score).forEach(s ->
				s.keySet().stream().forEach(x -> s.compute(x,(k,v) -> 0))
		);
		judge.keySet().stream().forEach(x->judge.compute(x,(k,v)->(k==MISS)?playedChart.chart.size:0));
	}
	public void     genNatural(double ratio) {
		setRecordable(false);
		setMaxCombo(0);
		if (ratio <= 0.0) {
			genMinimum();
		} else if (ratio >= 1.0) {
			genMaximum();
		} else {
			// Random Number Generator and Variable Constant References
			final Random RNG = new RandomXS128(System.nanoTime());
			final AtomicInteger atomicCombo = new AtomicInteger(0);
			final AtomicBoolean atomicJust  = new AtomicBoolean(false);
			// Define Constants
			final double
				spreadRatio = Math.pow(Math.log(ratio)/Math.log(0.5),2),
				EPSILON     = 1e-9,
				divisJST    = 5e-6,
				divisHIT    = 0.2,
				divisBAD    = 0.5,
				divisMIS    = 1.0;
			// Define Predicates of Reduction
			final BiPredicate<Double,Double>
				stateMIS    = (pr,cr)->((cr%divisMIS)>(pr%divisMIS))||
					(cr<=EPSILON&&cr<divisMIS-EPSILON),
				stateBAD    = (pr,cr)-> (cr%divisBAD)>(pr%divisBAD),
				stateHIT    = (pr,cr)-> (cr%divisHIT)>(pr%divisHIT),
				stateJST    = (pr,cr)->((cr%divisJST)>(pr%divisJST))&&
					((cr%(divisJST/10))>=(divisJST*0.005)+EPSILON)||
					((cr%(divisJST/10))<=(divisJST*0.095)-EPSILON);
			// Define Map of State-Predicates
			final Map<Judgement,BiPredicate<Double,Double>> stateCheck = new EnumMap<>(Judgement.class);
			{
				stateCheck.put(MISS,stateMIS);
				stateCheck.put(BAD ,stateBAD);
				stateCheck.put(HIT ,stateHIT);
			}
			// Define Action Chain
			final BiConsumer<Note,Judgement>
				attainPts = (n,j) ->
					addCounter(n.getType(),Math.round(j.baseScore * n.getType().scoreMult)),
				attainJdg = (n,j) ->
					addCounter(j),
				attainCmb = (n,j) -> {
					if (j.keepCombo) {
						if (atomicCombo.addAndGet(1) > getMaximumCombo())
							setMaxCombo(atomicCombo.get());
					} else {
						atomicCombo.set(0);
					}
				},
				attainJst = (n,j) -> {
					int r = Math.round(n.getType().scoreMult);
					if(j==EXCEL && atomicJust.get())
						while (r-- > 0) {
							addCounter(JUST);
							addJBonus(n.getType());
						}
				};
			// Define Note Iterator
			final Iterator<Note> noteIterator = playedChart.chart.iterator();
			// Generate any value from zero to one, and process them, until the end of iteration
			//System.out.printf("\033[34mPerfection rate of \033[1;32m%10.6f%%\033[34m:\033[m%n",ratio*100);
			RNG.doubles(playedChart.chart.size,0,1)
				.map(x -> Math.pow(x,spreadRatio))
				.reduce(1.0,(sumainder,mapped) -> {
					double next = (sumainder+mapped) % divisMIS;
					Note note = noteIterator.next();
					Judgement j = stateCheck.entrySet().stream()
						.filter(x ->
								x.getValue().test(sumainder,next) && sumainder < 1.0-EPSILON
						)
						.map(Map.Entry::getKey)
						.peek(x ->
								System.out.printf(true ? "" : "%-4s happened at Note[(%4d,%2d/%2d),%2d] with variance of %1.10f + %1.10f = %1.10f%n",
									x,
									note.getStart().getQuotient(),
									note.getStart().getRemainder(),
									note.getStart().getDivisor(),
									note.getPos(),
									sumainder,
									mapped,
									next
								)
						)
						.findFirst().orElse(EXCEL);
					atomicJust.set(stateJST.test(sumainder,next));
					attainPts.andThen(attainJdg).andThen(attainCmb).andThen(attainJst).accept(note,j);
					return next;
				});
			setSpeed(Math.round(getAchievement()/5f)/2f - 0.5f);
		}
	}
	public void     genMaximum() {
		setRecordable(false);
		setMaxCombo(0);
		setSpeed((float)Gameplay.setup.GSSN);
		Consumer<Note>
			attainPts = n -> addCounter(n.getType(),Math.round(EXCEL.baseScore * n.getType().scoreMult)),
			attainJdg = n -> addCounter(EXCEL),
			attainCmb = n -> setMaxCombo(getMaximumCombo()+1),
			attainJst = n -> {
				int r = Math.round(n.getType().scoreMult);
				while (r-- > 0) {
					addCounter(JUST);
					addJBonus(n.getType());
				}
			};
		Stream.of(playedChart.chart.toArray())
			.forEach(attainPts.andThen(attainJdg).andThen(attainCmb).andThen(attainJst));
	}
	public boolean  equals(Object o) {
		return (Objects.nonNull(o)) && (o instanceof GameplayResult) && (hashCode() == o.hashCode());
	}
	public int      hashCode() {
		long hash = playedTime.toEpochMilli();
		int  lhs,rhs;
		lhs = (int)(hash >>> 32L);
		rhs = (int)(hash & ( (~0L)>>>32 ));
		return lhs ^ rhs;
	}
	public String   toString() {
		return String.format("%s@%s",getClass().getSimpleName(),hashCode());
	}
	private GameplayResult determineNewSet() {
		playedSet = Chartset.find(playedChart);
		return this;
	}
	// <<END>> Instance Structure
	// Nested Classes
	public enum Rank {
		SSS("SS",+1,100f,true ,1.0f, .0f, .0f, .0f, .0f),
		SS ("SS", 0,100f,true , .0f, .0f, .0f, .0f, .0f),
		S  ( "S", 0, 99f,true , .0f, .0f, .0f, .0f, .0f),
		AA ("AA", 0, 97f,true , .0f, .0f, .0f, .0f, .0f),
		A  ( "A", 0, 95f,true , .0f, .0f, .0f, .0f, .0f),
		AM ( "A",-1, 90f,true , .0f, .0f, .0f, .0f, .0f),
		BP ( "B",+1, 85f,true , .0f, .0f, .0f, .0f, .0f),
		B  ( "B", 0, 80f,true , .0f, .0f, .0f, .0f, .0f),
		BM ( "B",-1, 75f,true , .0f, .0f, .0f, .0f, .0f),
		C  ( "C", 0, 70f,false, .0f, .0f, .0f, .0f, .0f),
		CM ( "C",-1, 60f,false, .0f, .0f, .0f, .0f, .0f),
		D  ( "D", 0, 40f,false, .0f, .0f, .0f, .0f, .0f),
		E  ( "E", 0, .0f,false, .0f, .0f, .0f, .0f, .0f);
		public static int compareTo(Rank r1, Rank r2) { return r1.compare(r2); }
		public final String  rankStr;
		public final float   accReq ;
		public final boolean passFlag;
		public final Map<Judgement,Float> judgementRequired;
		public int compare(Rank other) { return other.ordinal() - this.ordinal(); }
		Rank (String defString,int signSuffix,float accReq,boolean pass,double... judgeReq) {
			this.rankStr  = String.format("%s%s",defString,signSuffix!=0 ? String.format("%+d",signSuffix).charAt(0) : "");
			this.accReq   = accReq;
			this.passFlag = pass;
			EnumMap<Judgement,Float> mj = new EnumMap<>(Judgement.class);
			List<Judgement> lj = new ArrayList<>(Arrays.asList(Judgement.values()));
			//Collections.reverse(lj);
			Arrays.stream(judgeReq).forEach(rate -> mj.put(lj.remove(0),(float) rate));
			this.judgementRequired = Collections.unmodifiableMap(mj);
		}
	}
	// Constructors
	private GameplayResult(Chart diffRef) {
		this
			.setChart(diffRef)
			.setPlayTime(Instant.now())
			.setJustRef (new EnumMap<>(NoteType .class))
			.setScoreRef(new EnumMap<>(NoteType .class))
			.setJudgeRef(new EnumMap<>(Judgement.class));
		Stream.of(NoteType .values())
			.filter(k -> k.scoreMult > 0)
			.peek(k -> just.computeIfAbsent(k,(t) -> 0))
			.peek(k -> score.computeIfAbsent(k,(t) -> 0))
			.reduce(null,(p,c)->c);
		Stream.of(Judgement.values())
			.peek(k->judge.computeIfAbsent(k,(t)->0))
			.reduce(null,(p,c)->c);
	}
	// Driver
}
