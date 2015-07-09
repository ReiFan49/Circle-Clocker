package com.rfhkr.cc.gameplay;

import com.badlogic.gdx.utils.*;
import com.rfhkr.cc.errors.*;
import com.rfhkr.util.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.rfhkr.cc.gameplay.CursorHandler.Mode.*;

/**
 * @author Rei_Fan49
 * @since 2015/07/01
 */
final class CursorHandler {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static Mode type = Mode.SINGLE;
	// ** ACCESSORS
	public static Mode type() { return type; }
	public static Mode type(Mode type) { return CursorHandler.type=type; }
	// ** PREDICATES
	private static void get(final int mode,int pos, Mode cursor, Byte[] buffer){
		if(Math.min(mode,pos)<=0 || Math.max(mode,pos)>= Byte.MAX_VALUE)
			throw new ArithmeticException("bad number");
		pos += mode-1;
		Array<Byte> tempAry = new Array<>(cursor.num);
		switch(cursor) {
			case SINGLE:
				tempAry.add((byte)pos);
				break;
			case SPREAD:
				tempAry.addAll((byte)--pos,(byte)++pos,(byte)++pos);
				break;
			case CROSSED:
				tempAry.addAll((byte)(pos-1),(byte)(pos+1));
				pos += mode>>1;
				tempAry.addAll((byte)(pos-1),(byte)(pos+1));
				break;
			default:
				throw new IllegalArgumentException("Not specified cursor mode!");
		}
		tempAry.addAll((byte)-1,(byte)-1,(byte)-1,(byte)-1);
		System.arraycopy(
			Arrays.stream(tempAry.toArray()).map(x -> (byte) ((x % mode) + 1)).toArray(Byte[]::new),0,
			buffer,0,
			buffer.length
		);
	}
	private static void get(final int mode,int pos, Byte[] buffer) {
		get(mode,pos,type,buffer);
	}
	public static void get(int pos,Byte[] buffer) {
		get(Gameplay.now().getMode(),pos,buffer);
	}
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	// <<END>> Instance Structure
	// Nested Classes
	enum Mode {
		SINGLE(1), SPREAD(3), CROSSED(4);
		public final int num;
		Mode(int i) {num=i;}
	}
	static final class ChartSolver {
		private static final Map<Float,boolean[]> input = new TreeMap<>();
		private static final Map<Float,Pair<Mode,Byte>> movement = new TreeMap<>();
		private static final Comparator<Pair<Mode,Byte>> pcomp = (pre,cur)-> {
			int c;
			c = Integer.compare(pre.get1st().ordinal(),cur.get1st().ordinal());
			return c!=0 ? c : Integer.compare(pre.get2nd(),cur.get2nd());
		};
		static Map<Float,Pair<Mode,Byte>> getSolution() {
			return Collections.unmodifiableMap(movement);
		}
		public static int moveCount(Pair<Mode,Byte> prev,Pair<Mode,Byte> curr) {
			final int w = input.get(-0.001f).length;
			int rate = 0,diff = Math.abs(prev.get2nd() - curr.get2nd());
			// Get M-G-R style of difference
			/** Sample Case:
			 *  1 2 3 4 5 6 7 8 (w=8), 2 -> 8 = 2
			 *  8 - 6 = 2, w - d = rd
			 */
			diff = diff > (w>>1) ? w-diff : diff;
			// Count the state difference of the cursor
			rate += Math.abs(prev.get1st().ordinal() - curr.get1st().ordinal());
			// Calculate for cursor movement difference as 2 * N * N (which increases exponentially)
			rate += 2 * (int)Math.pow(diff,2);
			return rate;
		}
		/** removes all recorded state */
		public static void clear() { input.clear(); movement.clear(); }
		/** add a state required */
		public static void send(final float time, final boolean[] state) {
			if(false)
				// Simply ignore any initial state
				if(time<0.0f) return;
			// Asserts the state for solvable
			simpleSolve(state);
			input.put(time,state);
			//System.out.println(simpleSolve(time));
		}
		/** check validity */
		private static boolean isValid(int pos, Mode cursor, final boolean[] stateCheck) {
			if(cursor==SINGLE) return false;
			// Sample case:
			// Size 8, Cursor CROSS, At 4 (Hov. 1 3 5 7)
			// Given State 1 5 7
			// Must not touch 2 4 6 8
			Byte[] getHover = new Byte[]{-1,-1,-1,-1};
			boolean[] stateHover = new boolean[stateCheck.length];
			get(stateCheck.length,pos,cursor,getHover);
			for(byte i:getHover) if(i>0) stateHover[--i]=true;
			/** stateCheck case: true  false false false true  false true  false */
			/*  stateHover case: true  false true  false true  false true  false */
			boolean result = true;
			// False -> Not Hover but Check State
			/* Truth Table
				 Chk Hov Res
				  F   T   T
				  F   F   T
				  T   T   T
				  T   F   F
				 Impliq:A->B <=> (~A || B)
			 */
			for(int i=0; i<stateCheck.length; i++)
				result &= !stateCheck[i] || stateHover[i];
			return result;
		}
		/** fresh validity check, instant error if invalid */
		private static void simpleSolve(final boolean[] stateGiven) {
			boolean solvable = false;
			for(Mode cursor:Mode.values())
				for(byte pos=1;pos<=stateGiven.length;pos++)
					solvable |= isValid(pos,cursor,stateGiven);
			if (!solvable)
				throw InvalidSequenceException.invoke(
					String.format("Given state %s cannot be solved through perfect-run.",Arrays.toString(stateGiven))
				);
		}
		public static Set<Pair<Mode,Byte>> simpleSolve(float time) {
			Set<Pair<Mode,Byte>> result = new TreeSet<>(pcomp);
			boolean[] stateGiven = input.get(time);
			for(Mode cursor:Mode.values())
				for(byte pos=1;pos<=stateGiven.length;pos++)
					if(isValid(pos,cursor,stateGiven))
						result.add(Pair.gen(cursor,pos));
			return result;
		}
		private static int min(int... numbers) {
			switch(numbers.length){
				case 0: throw new IllegalArgumentException("Empty min");
				case 1: return numbers[0];
				case 2: return Math.min(numbers[0],numbers[1]);
				default: return min(
					min(Arrays.copyOfRange(numbers,0,numbers.length >> 1)),
					min(Arrays.copyOfRange(numbers,numbers.length >> 1,numbers.length))
				);
			}
		}
		private static int min(boolean[] prevState, Pair<Mode,Byte> state) {
			final byte w = (byte)prevState.length;
			@SuppressWarnings("unchecked")
			final Pair<Mode,Byte>[] pConst = simpleSolve(-0.001f).toArray(new Pair[w]);
			int[] moves = new int[w];
			for(int i=0;i<w;i++)
				moves[i] = prevState[i] ? moveCount(pConst[i],state) : 65535;
			return min(moves);
		}
		/** try to solve in lowest possible number of moves */
		public static void solve() {
			movement.clear();

			Float cprev,ctime;
			Iterator<Float> solcheck = input.keySet().iterator();
			final int
				// Steps
				s = input.size(),
				// Width
				w = input.get(-0.001f).length * 2;
			Set<Pair<Mode,Byte>> csol = null;
			int    [][] moveCount = new int    [s][w];
			int    [][] movePred  = new int    [s][w];
			boolean[][] moveOK    = new boolean[s][w];
			// Initialize value, 65535 treated as Infinity, as possible values are
			// 2n^2+c, n from 0 to FIELD / 2, c from 0 to 1
			/** how to read moveCount and movePred indices
					[ y ] [ x ]
			 		@param y is the step counter, zero is for start
			 		@param x is for condition index, which we will play off from here,
						SPREAD ones have    0   <= x < (w>>2)
			 			CROSSD ones have (w>>2) <= x <   w
			 */
			for(int n=0;n<s;n++)
				for(int i=0;i<w;i++) {
					moveCount[n][i] = n==0?0:65535;
					movePred [n][i] = -128;
					moveOK   [n][i] = (n==0);
				}
			if(false)
				input.forEach((t,ss)->System.out.printf("%7.3f -> %s%n",t,Arrays.toString(ss)));
			if(input.isEmpty())
				throw ReiException.invoke("Nothing to solve");
			int stepCount = 0;
			csol = simpleSolve(solcheck.next());
			@SuppressWarnings("unchecked")
			final Pair<Mode,Byte>[] pConst = csol.toArray(new Pair[w]);
			// Fill all the movePair data first
			while(solcheck.hasNext()) {
				stepCount++;
				ctime = solcheck.next();
				csol = simpleSolve(ctime);
				int i;
				for(Pair<Mode,Byte> p:csol) {
					// Get current index
					i = (p.get1st() == CROSSED ? (w >> 1) : 0) + p.get2nd() - 1;
					// Set as passable movement
					moveOK[stepCount][i] = true;
					// Set initial move count required
					moveCount[stepCount][i] = min(moveOK[stepCount-1],p);
					// Find first index that suspected as the lowest move required one
					int nextMove = 0;
					for(int j=1;j<w&&nextMove==0;j++)
						nextMove = (moveOK[stepCount-1][j]&&moveCount(pConst[j],p)==moveCount[stepCount][i]) ? j : nextMove;
					// Set predecessor
					movePred[stepCount][i] = nextMove;
					// Add the predecessor move counter to the current one
					moveCount[stepCount][i] += moveCount[stepCount-1][nextMove];
				}
			}
			int j = 0;
			for(int i=s-1;i>=1;i--) {
				if(false) {
					System.out.printf("Step \u001b[1;31m%4d\u001b[m @%7.3fsec%n",i,input.keySet().toArray()[i]);
					System.out.println(Arrays.stream(movePred[i]).mapToObj(x -> String.format("%5d",x)).collect(Collectors.joining()));
					System.out.println(Arrays.stream(moveCount[i]).mapToObj(x -> String.format("%5d",x)).collect(Collectors.joining()));
					System.out.println();
				}
				if(i==s-1)
					for(int k=1;k<w;k++)
						j = (Integer.compare(moveCount[i][j],moveCount[i][k])==1) ? k : j;
				else
					j = movePred[i+1][j];
				movement.put((Float)input.keySet().toArray()[i-1],pConst[ movePred[i][j] ]);
			}
		}
		private ChartSolver() {}
	}
	// Constructors
	private CursorHandler() {}
	// Driver
	public static void main(String... argv) {
		Byte[] b;
		b = new Byte[]{-1,-1,-1,-1};
		Consumer<Byte[]> p = (ary)->
			System.out.println(Arrays.stream(ary).map(Object::toString).collect(Collectors.joining(",")));
		Stream.of(8,16).forEach((k)->
			Stream.of(SPREAD,CROSSED).forEach((m)->
				Stream.of(1,2,3,4,6,8).forEach((po)-> {
					type(m); get(k,po,b); p.accept(b);
				})
			)
		);
	}
}
