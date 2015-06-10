package com.rfhkr.cc.level;

import com.rfhkr.util.*;

import java.util.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/05
 */
public class Timing {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	private static final Map<Pair<Short,Twin<Byte>>,Timing> cache = new TreeMap<>((a,b)-> {
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
	// ** METHODS
	public static Timing at(int beat) throws Exception {
		return at(beat,0,1);
	}
	public static Timing at(int beat,int dividend,int divisor) throws Exception {
		return at((short)beat,(byte)dividend,(byte)divisor);
	}
	public static Timing at(short beat,byte dividend,byte divisor) throws Exception {
		// Assertion Check
		if(dividend< 0)
			throw new Exception("dividend is either zero or a positive integer");
		if(divisor <=0)
			throw new Exception("divisor must be a positive integer");
		if(dividend>=divisor)
			throw new Exception("dividend must less than divisor");
		byte ld = divisor;
		while(ld>=4) {
			if((ld&1)==1)
				throw new Exception("divisor ("+divisor+") must be power amplification of 2 or 3, or 1.");
			else
				ld>>=1;
		}
		// Perform Binary GCD
		byte dv=dividend,dd=divisor,g=0,b  = 0;
		if(dv>0) {
			while ((dv | dd) % 2 == 0) {
				dv >>= 1;
				dd >>= 1;
				b++;
			}
			while (dv != dd) {
				if (dv% 2 == 0) dv >>= 1;
				else if (dd % 2 == 0) dd >>= 1;
				else if (dv > dd) dv = (byte) ((dv-dd) >> 1);
				else dd = (byte) ((dd-dv) >> 1);
			}
			g = (byte)(dv << b);
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
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	/** bar */
	private short b;
	/** divisor and dividend */
	private byte dv,dd;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public float  toFloat(BPMData bpm) { return (float)toDouble(bpm); }
	public double toDouble(BPMData bpm) {
		return (60.0/bpm.getBPM()) * (b + ((double)dv/dd));
	}
	public String toString() {
		return String.format("[%s Object: @beat %d, division %d/%d]",
			this.getClass().getSimpleName(),(int)this.b,(int)this.dv,(int)this.dd);
	}
	// <<END>> Instance Structure
	// Constructors
	private Timing(short b,byte dv,byte dd) {
		this.b = b; this.dv = dv; this.dd = dd;
	}
	// Driver
}
