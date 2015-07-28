package com.rfhkr.cc.gameplay.result;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.errors.*;
import com.rfhkr.cc.gameplay.*;
import static com.rfhkr.cc.gameplay.Judgement.*;
import com.rfhkr.cc.level.*;
import com.rfhkr.util.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/26
 */
public final class Highscore {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static final Highscore self = new Highscore();
	private static final AtomicInteger r = new AtomicInteger(1);
	// ** ACCESSORS
	public static Highscore get() {
		return self;
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private final Map<Chart,Set<GameplayResult>> scores;
	// ** ACCESSORS
	public Set<GameplayResult> getScores(Chart song){
		return scores.computeIfAbsent(song,(k)->{
			Set<GameplayResult> x = new TreeSet<>(GameplayResult::compare);
			// TEST MODE -- Generate SS+, and lower
			Array<Double> ratio = Array.with(
				1.0000,0.9999,0.9996,0.9993,0.9990,
				0.9980,0.9960,0.9930,0.9910,0.9900,
				0.9800,0.9700,0.9500,0.9250,0.9000,
				0.8750,0.8500,0.8250,0.8000,0.7500);
			GameplayResult g;
			while (ratio.size > 0) {
				double r = ratio.removeIndex(0);
				g = GameplayResult.make(k);
				g.genNatural(r);
				if(!x.add(g)&&false)
					System.out.printf("Perfection ratio at \033[1;31m%1.8f%% rejected\033[m -> [%s]%n",r*100,
						g.getTotalScore()
					);
			}
			return x;
		});
	}
	public void clearScores(){
		scores.clear();
	}
	public void clearScores(Chart song){
		scores.computeIfPresent(song,(k,v)->{v.clear();return v;});
	}
	/**
	 * get Top N highscore
	 * @param song      chart that you need to check
	 * @param limit     limits the data received
	 * @param includeNG include non recordable
	 * @return T<sub>limit</sub> highscore
	 */
	public Set<GameplayResult> getT(Chart song, int limit,boolean includeNG) {
		return getTFrom(song,1,limit,includeNG);
	}
	/**
	 * get Paged Top N highscore
	 * @param song      chart that you need to check
	 * @param from      pagination starts
	 * @param to        pagination ends
	 * @param includeNG include non recordable
	 * @return T<sub>limit</sub> highscore
	 */
	public Set<GameplayResult> getTFrom(Chart song, int from, int to,boolean includeNG) {
		return Collections.unmodifiableSortedSet(
			getScores(song)
				.stream()
				.skip(from - 1)
				.filter(x -> includeNG || x.isRecordable())
				.limit(to - (from - 1))
				.collect(Collectors.toCollection(TreeSet::new))
		);
	}
	public Set<GameplayResult> getTPage(Chart song, int page, int size,boolean includeNG) {
		return getTFrom(song,(page-1)*size+1,page*size,includeNG);
	}
	/**
	 * return topmost highscore that either fulfilling the REC OK / REC NG condition itself
	 * @param song      picked chart
	 * @param includeNG allows non recordable record shown
	 * @return TOP 1 of the chart
	 * @throws NoRecordException  if the chart is empty
	 */
	public GameplayResult getT1(Chart song,boolean includeNG) {
		return getT(song,1,includeNG).stream().findFirst().orElseThrow(() ->
			NoRecordException.invoke("Current chart does not have any records.")
		);
	}

	/**
	 * return rank N of the highscore that either fulfilling the REC OK / REC NG condition itself
	 * @param song      picked chart
	 * @param rank      rank lookup
	 * @param includeNG allows non recordable record shown
	 * @return RANK <em>N</em> of the chart
	 * @throws NoRecordException  if the chart does not have a highscore at rank <em>N</em>
	 */
	public GameplayResult getR(Chart song,int rank,boolean includeNG) {
		return getTFrom(song,rank-1,rank,includeNG).stream().findFirst().orElseThrow(() ->
			NoRecordException.invoke("Current chart record does not reach rank "+rank)
		);
	}
	public int getR(Chart song,GameplayResult result,boolean includeNG) {
		r.set(0);
		getScores(song).stream()
			.filter(x->x.isRecordable() || includeNG)
			.peek(x->r.incrementAndGet())
			.filter(x->x.equals(result))
			.findFirst();
		return r.updateAndGet(x->x==getScores(song).size() ? 0 : x);
	}
	public String show(Chart diff,int limit,boolean includeNG) {
		return show(diff,1,limit,includeNG);
	}
	public String show(Chart diff,int from,int to,boolean includeNG) {
		r.set(from-1);
		return getTFrom(diff,from,to,includeNG).stream().map(x ->
				String.format("#%d %s GS%s %09dpts %06.2f%% %04dcombo (%s) %s %d(+%1.1f)/%d/%d/%d",
					r.incrementAndGet(),
					x.getPlayer(),
					x.getSpeedStr(),
					x.getTotalScore(),
					x.getAchievement(),
					x.getMaximumCombo(),
					x.getRank().rankStr,
					x.getRank().passFlag ? "PASS" : "FAIL",
					x.getJudgeRef().get(EXCEL),
					x.getJudgeRef().get(JUST)/10f,
					x.getJudgeRef().get(HIT),
					x.getJudgeRef().get(BAD),
					x.getJudgeRef().get(MISS)
				)
		).collect(Collectors.joining("\n"));
	}
	public String showPage(Chart diff,int page,int size,boolean includeNG) {
		return show(diff,(page-1)*size+1,page*size,includeNG);
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Classes
	// Constructors
	private Highscore() {
		scores = new TreeMap<>(Chart::compare);
	}
	// Driver
}
