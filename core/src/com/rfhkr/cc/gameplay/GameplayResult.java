package com.rfhkr.cc.gameplay;

import com.rfhkr.cc.level.*;
import com.sun.istack.internal.*;
import static com.rfhkr.cc.level.Chart.NoteType;
import static com.rfhkr.cc.gameplay.Judgement.*;

import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/16
 */
public final class GameplayResult {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static GameplayResult make(Chart diff) {
		return new GameplayResult(diff);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	@NotNull
	private Chartset playedSet;
	@NotNull
	private Chart playedChart;
	private EnumMap<NoteType ,Integer> score;
	private EnumMap<NoteType ,Integer> just ;
	private EnumMap<Judgement,Integer> judge;
	private int   maximumScore;
	private int   maximumCombo;
	// ** ACCESSORS
	public Chartset getChartset() { return playedSet; }
	public Chart    getChart   () { return playedChart; }
	public EnumMap<NoteType ,Integer> getScoreRef() { return score; }
	public EnumMap<NoteType ,Integer> getJustRef () { return just ; }
	public EnumMap<Judgement,Integer> getJudgeRef() { return judge; }
	public int      getMaximumScore() { return maximumScore; }
	public int      getMaximumCombo() { return maximumCombo; }
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
	// ** INTERACTIONS
	// ** METHODS
	public void     addJBonus (NoteType  e) { just.put(e,just.getOrDefault(e,0)+50); }
	public void     addCounter(NoteType  e,int value) { score.put(e,score.getOrDefault(e,0)+value); }
	public void     addCounter(Judgement e) { judge.put(e,judge.getOrDefault(e,0)+1); }
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
	private GameplayResult determineNewSet() {
		playedSet = Chartset.find(playedChart);
		return this;
	}
	// <<END>> Instance Structure
	// Nested Classes
	public static enum Rank {
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
			Collections.reverse(lj);
			Arrays.stream(judgeReq).forEach(rate -> mj.put(lj.remove(0),(float) rate));
			this.judgementRequired = Collections.unmodifiableMap(mj);
		}
	}
	// Constructors
	private GameplayResult(Chart diffRef) {
		this
			.setChart(diffRef)
			.setJustRef (new EnumMap<>(NoteType .class))
			.setScoreRef(new EnumMap<>(NoteType .class))
			.setJudgeRef(new EnumMap<>(Judgement.class));
	}
	// Driver
}
