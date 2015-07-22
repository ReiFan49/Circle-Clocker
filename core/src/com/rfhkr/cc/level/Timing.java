package com.rfhkr.cc.level;

import com.rfhkr.util.*;
import com.sun.istack.internal.*;

import java.util.*;
import java.util.function.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/05
 */
public class Timing implements Comparable<Timing>{
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static transient final Map<Pair<Short,Twin<Byte>>,Timing> cache = new TreeMap<>((a,b)-> {
		int c;
		if((c = Short.compare(a.get1st(),b.get1st()))!=0) return c;
		if((c = Byte .compare(a.get2nd().get1st(),b.get2nd().get1st()))!=0) return c;
		return Byte.compare(a.get2nd().get2nd(),b.get2nd().get2nd());
	});
	// ** ACCESSORS
	public static Map<Pair<Short,Twin<Byte>>,Timing> getCache() {
		return Collections.unmodifiableMap(cache);
	}
	// ** PREDICATES
	// ** INTERACTIONS
	public static int compare(Timing t1,Timing t2) { return t1.compareTo(t2); }
	// ** METHODS
	public static Timing at(int beat) {
		return at(beat,0,1);
	}
	public static Timing at(int beat,int dividend,int divisor) {
		return at((short)beat,(byte)dividend,(byte)divisor);
	}
	public static Timing at(short beat,byte dividend,byte divisor) {
		// Assertion Check
		if(dividend< 0)
			throw new ArithmeticException("dividend is either zero or a positive integer ");
		if(divisor <=0)
			throw new ArithmeticException("divisor must be a positive integer");
		if(dividend>divisor)
			throw new ArithmeticException(String.format("dividend must less than divisor (%d/%d)",dividend,divisor));
		else if (dividend==divisor)
			return at(beat+1,0,1);
		byte ld = divisor;
		while(ld>=4) {
			if((ld&1)==1)
				throw new ArithmeticException("divisor ("+divisor+") must be power amplification of 2 or 3, or 1.");
			else
				ld>>=1;
		}
		// Perform Binary GCD
		if(dividend>0) {
			byte g = (byte)MathSim.gcd(dividend,divisor);
			dividend /= g;
			divisor  /= g;
		} else {
			divisor = 1;
		}
		// Perform Basic Check
		Pair tp = Pair.gen(beat, Twin.set(dividend, divisor));
		// Check timing cache
		Timing t;
		if(cache.containsKey(tp)) {
			t = cache.get(tp);
		} else {
			t = new Timing(beat, dividend, divisor);
			cache.put(tp, t);
		}
		return t;
	}
	public static Timing interval(Timing t1,Timing t2) {
		Twin<Integer> tf1 = t1.getRational();
		Twin<Integer> tf2 = t2.getRational();
		Twin<Integer> tr  = MathSim.fractionSub(tf1.get1st(),tf1.get2nd(), tf2.get1st(),tf2.get2nd());
		//try {
			return Timing.at(tr.get1st() / tr.get2nd(), tr.get1st() % tr.get2nd(), tr.get2nd());
		//} catch(Exception e) { System.err.printf("Bad fraction %s - %s => %s%n", t1, t2, e); return null; }
	}
	public static Timing shift(Timing t1,Timing t2) {
		Twin<Integer> tf1 = t1.getRational();
		Twin<Integer> tf2 = t2.getRational();
		Twin<Integer> tr  = MathSim.fractionAdd(tf1.get1st(), tf1.get2nd(), tf2.get1st(), tf2.get2nd());
		//try {
			return Timing.at(tr.get1st() / tr.get2nd(), tr.get1st() % tr.get2nd(), tr.get2nd());
		//} catch(Exception e) { System.err.printf("Bad fraction %s + %s => %s%n", t1, t2, e); return null; }
	}
	public static Timing valueOf(double realNum) { return valueOf(realNum,16); }
	public static Timing valueOf(double realNum,int  maxdiv) { return valueOf(realNum,(byte)maxdiv); }
	public static Timing valueOf(double realNum,byte maxdiv) {
		/** Check Precision **/
		double rn = realNum;
		short b = 0; byte dv = 0, dd = 1;
		final double EPSILON = 1.0e-3;
		boolean harshpprox = false;
		/** Lambda Functions **/
		DoublePredicate approximation = (val)->val<=EPSILON || val>=1-EPSILON;
		BiFunction<Double,Byte,Double> remainder = (val,dn)->((val*dn)%1);
		BiPredicate<Double,Byte> divisable = (val,dn) -> approximation.test(remainder.apply(val,dn));
		b = (short)rn;
		rn -= b;
		/** Find the dividend **/
		if(!divisable.test(rn,dd)) {
			while (true) {
				/** Checks divisable by 2 **/
				dd  <<= 1;
				if(divisable.test(rn,dd)) {
					dv = (byte)Math.round(remainder.apply(rn,(byte)1)*dd);
					break;
				}
				dd >>>= 1;
				/** Checks divisable by 3 **/
				dd   *= 3;
				if(divisable.test(rn,dd)) {
					dv = (byte)Math.round(remainder.apply(rn,(byte)1)*dd);
					break;
				}
				dd   /= 3;
				/** Fails to meet the requirement, advance the iteration by mult of 2 **/
				dd  <<= 1;
				if(dd>=maxdiv) {
					dv = (byte)Math.round(rn * dd);
					harshpprox = true;
					break;
				}
			}
		}
		if(rn>=1-EPSILON) b++;
		return Timing.at(b,dv,dd);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	/** bar */
	private short b;
	/** divisor and dividend */
	private byte dv,dd;
	// ** ACCESSORS
	private int getQuotient()  { return b;  }
	private Twin<Integer> getRational() { return Twin.set(b*dd+dv,0+dd); }
	private int getRemainder() { return dv; }
	private int getDivisor()   { return dd; }
	// ** PREDICATES
	// ** INTERACTIONS
	public int    compareTo(@Nullable Timing other) {
		return Objects.nonNull(other) ? Double.compare(this.toDouble(BPMData.on()),other.toDouble(BPMData.on())) : 1;
	}
	public boolean equals(Object other) {
		return (other != null) && (other instanceof Timing) && equals((Timing) other);
	}
	public boolean equals(Timing other) {
		return this.compareTo(other) == 0;
	}
	// ** METHODS
	public float  toFloat(BPMData bpm) { return (float)toDouble(bpm); }
	public float  toFloat(Timing other,BPMData bpm) { return Timing.interval(this,other).toFloat(bpm); }
	public double toDouble(BPMData bpm) {	return (60.0/bpm.getBPM()) * (b + (double)dv/(double)dd); }
	public double toDouble(Timing other, BPMData bpm) { return Timing.interval(this,other).toDouble(bpm); }
	public String toString() {
		return String.format("[%s: @beat %4d, division %02d/%02d]",
			this.getClass().getSimpleName(),(int)this.b,(int)this.dv,(int)this.dd);
	}
	// <<END>> Instance Structure
	// Nested Class
	private static final class MathSim {
		public static final long gcd(long a,long b) {
			if ((a^b)==0) return a;
			if ((a^b)==a) return a;
			if ((a^b)==b) return b;
			if ((~a & 1) == 1)
				return (b&1)==1 ? gcd(a>>1,b) : gcd(a>>1,b>>1)<<1;
			if ((~b & 1) == 1)
				return gcd(a,b>>1);
			return (a>b) ? gcd((a-b)>>1,b) : gcd((b-a)>>1,a);
			/* iterative
			long p = 0;
			while ((a | b) % 2 == 0) {
				a >>= 1;
				b >>= 1;
				p++;
			}
			while ((a^b)==0) {
				if (a%2 == 0) a >>= 1;
				else if (b % 2 == 0) b >>= 1;
				else if (a > b) a = ((a-b) >> 1);
				else b = ((b-a) >> 1);
			}
			return (a <<= p); */
		}
		public static final Twin<Integer> fractionAdd(int n1, int d1, int n2, int d2) {
			return Twin.set((n1 * d2 + n2 * d1)/(int)gcd(d1, d2), (d1 * d2) / (int) gcd(d1, d2));
		}
		public static final Twin<Integer> fractionAdd(Twin<Integer>... fracs) {
			return Arrays.stream(fracs).reduce(Twin.set(0,1),
				(pre,cur)->fractionAdd(pre.getX(),pre.getY(),cur.getX(),cur.getY())
			);
		}
		public static final Twin<Integer> fractionSub(int n1, int d1, int n2, int d2) {
			return fractionAdd(n1, d1, -n2, d2);
		}
		public static final Twin<Integer> fractionSub(Twin<Integer> init,@NotNull Twin<Integer>... fracs) {
			return Arrays.stream(fracs).reduce(init.swapPair(true).swapPair(),
				(pre,cur)->fractionSub(pre.getX(),pre.getY(),cur.getX(),cur.getY())
			);
		}
		public static final Twin<Integer> fractionMul(int n1, int d1, int n2, int d2) {
			return Twin.set((n1*n2)/ (int) gcd(d1, d2),(d1*d2)/ (int) gcd(d1, d2));
		}
	}
	// Constructors
	private Timing(short b,byte dv,byte dd) {
		this.b = b; this.dv = dv; this.dd = dd;
	}
	// Driver
}
