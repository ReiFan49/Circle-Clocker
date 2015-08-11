package com.rfhkr.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 *  performs bit manipulation on number data
 *  @author Rei Hakurei
**/
public final class BitOperator {
	int  iBits;
	long lBits;
	private BitOperator() {iBits = 0; lBits = 0L;}
	private BitOperator(int  x) {this(); iBits = x;}
	private BitOperator(long x) {this(); lBits = x;}
	
	
	/** reads the <i>i</i>th bit of <i>x</i>, which <i>i</i> starts from the least significant bit.
	 *  @param  x number that given for bit-reading
	 *  @param  i byte index to be returned (0th index at least significant bit)
	 *  @return byte-code either <strong>1</strong> or <strong>0</strong> returned from the <i>i</i>th bit of x
	**/
	public static byte    readBit (int    x, Byte    i) { return (byte)(x & (1 << i)); }
	/** reads the <i>i</i>th bit of <i>x</i>, which <i>i</i> starts from the least significant bit.
	 *  @param  x number that given for bit-reading
	 *  @param  i byte index to be returned (0th index at least significant bit)
	 *  @return byte-code either <strong>1</strong> or <strong>0</strong> returned from the <i>i</i>th bit of x
	**/
	public static byte    readBit (long   x, Byte    i) { return (byte)(x & (1 << i)); }
	// Aliases
	/** shorthand for the <i>byte-sized</i> index, typecasting performed.
	 *  @param  x value to read
	 *  @param  i on the <i>i</i>th bit
	 *  @return <i>i</i>th byte of the <i>x</i>, with <i>i</i> starts from the least significant one
	 *  @see #readBit(int ,Byte)
	**/
	public static Byte    readBit (int    x, Integer i) { return readBit(x,i.byteValue()); }
	/** shorthand for the <i>byte-sized</i> index, typecasting performed.
	 *  @param  x value to read
	 *  @param  i on the <i>i</i>th bit
	 *  @return <i>i</i>th byte of the <i>x</i>, with <i>i</i> starts from the least significant one
	 *  @see #readBit(long,Byte)
	**/
	public static Byte    readBit (long   x, Integer i) { return readBit(x,i.byteValue()); }

	/** overwrites the <i>i</i>th bit of <i>x</i> with <b>bit</b> code, which <i>i</i> starts from the least significant bit.
	 *  @param  x   number that given for bit-reading
	 *  @param  i   byte index for editing (0th index at least significant bit)
	 *  @param  bit value for overwriting the <i>i</i>th byte of the number
	 *  @return modified of <i>x</i> with the <i>i</i>th byte replaced with <i>bit</i> value.
	 *  @see    #writeBit(int,byte,boolean)
	**/
	public static int writeBit(int    x, byte    i, byte    bit) { return (x & ~(1<<i))|(     bit << i     ); }
	/** overwrites the <i>i</i>th bit of <i>x</i> with <b>bit</b> code, which <i>i</i> starts from the least significant bit.
	 *  @param  x   number that given for bit-reading
	 *  @param  i   byte index for editing (0th index at least significant bit)
	 *  @param  bit value for overwriting the <i>i</i>th byte of the number
	 *  @return modified of <i>x</i> with the <i>i</i>th byte replaced with <i>bit</i> value.
	 *  @see    #writeBit(int,byte,byte)
	**/
	public static int writeBit(int    x, byte    i, boolean bit) { return (x & ~(1<<i))|((bit ? 1 : 0) << i); }
	/** overwrites the <i>i</i>th bit of <i>x</i> with <b>bit</b> code, which <i>i</i> starts from the least significant bit.
	 *  @param  x   number that given for bit-reading
	 *  @param  i   byte index for editing (0th index at least significant bit)
	 *  @param  bit value for overwriting the <i>i</i>th byte of the number
	 *  @return modified of <i>x</i> with the <i>i</i>th byte replaced with <i>bit</i> value.
	 *  @see    #writeBit(long,byte,boolean)
	**/
	public static long writeBit(long x, byte i, byte bit) { return (x & ~(1<<i))|(     bit << i     ); }
	/** overwrites the <i>i</i>th bit of <i>x</i> with <b>bit</b> code, which <i>i</i> starts from the least significant bit.
	 *  @param  x   number that given for bit-reading
	 *  @param  i   byte index for editing (0th index at least significant bit)
	 *  @param  bit value for overwriting the <i>i</i>th byte of the number
	 *  @return modified of <i>x</i> with the <i>i</i>th byte replaced with <i>bit</i> value.
	 *  @see    #writeBit(long,byte,byte)
	**/
	public static long writeBit(long x, byte i, boolean bit) { return (x & ~(1<<i))|((bit ? 1 : 0) << i); }
	// Aliases
	/** shorthand for the <i>byte-sized</i> index bit-modification, typecasting performed.
	 *  @param  x   base value
	 *  @param  i   index to modify the bit
	 *  @param  bit modifier value
	 *  @return modified <i>x</i> with <i>i</i>th bit (0th is most insignificant) replaced with <i>bit</i> value
	 *  @see #writeBit(int,byte,byte)
	 *  @see #writeBit(int,byte,boolean)
	 *  @see #writeBit(int,int,boolean)
	**/
	public static int writeBit(int x, int i, int bit) { return writeBit(x,(byte)i,(byte)bit); }
	/** shorthand for the <i>byte-sized</i> index bit-modification, typecasting performed.
	 *  @param  x   base value
	 *  @param  i   index to modify the bit
	 *  @param  bit modifier value
	 *  @return modified <i>x</i> with <i>i</i>th bit (0th is most insignificant) replaced with <i>bit</i> value
	 *  @see #writeBit(int,byte,byte)
	 *  @see #writeBit(int,byte,boolean)
	 *  @see #writeBit(int,int,int)
	**/
	public static int writeBit(int x, int i, boolean bit) { return writeBit(x,(byte)i,bit); }
	/** shorthand for the <i>byte-sized</i> index bit-modification, typecasting performed.
	 *  @param  x   base value
	 *  @param  i   index to modify the bit
	 *  @param  bit modifier value
	 *  @return modified <i>x</i> with <i>i</i>th bit (0th is most insignificant) replaced with <i>bit</i> value
	 *  @see #writeBit(long,byte,byte)
	 *  @see #writeBit(long,byte,boolean)
	 *  @see #writeBit(long,int,boolean)
	**/
	public static long writeBit(long x, int i, int bit) { return writeBit(x,(byte)i,(byte)bit); }
	/** shorthand for the <i>byte-sized</i> index bit-modification, typecasting performed.
	 *  @param  x   base value
	 *  @param  i   index to modify the bit
	 *  @param  bit modifier value
	 *  @return modified <i>x</i> with <i>i</i>th bit (0th is most insignificant) replaced with <i>bit</i> value
	 *  @see #writeBit(long,byte,byte)
	 *  @see #writeBit(long,byte,boolean)
	 *  @see #writeBit(long,int,int)
	**/
	public static long    writeBit(long   x, int     i, boolean bit) { return writeBit(x,(byte)i,bit); }
	
	
	/** 
	 *  shifts all bit to the Left, leftmost <i>len</i> bits are moved to less significant position in order
	 *  @param x   the number
	 *  @param len how many bits that you want to take
	 *  @return number that have its bit <i>ringly-shifted</i> to the left
	**/
	public static Integer ringOpLeft(Integer x, Byte len) {
		int
			byt = 32,
			inv = -1,
			tak = x & (inv << len),
			rem = x & ~tak;
		return  (rem << (byt-len)) | (tak >>> len);
	}
	/** 
	 *  shifts all bit to the Left, leftmost <i>len</i> bits are moved to less significant position in order
	 *  @param x   the number
	 *  @param len how many bits that you want to take
	 *  @return number that have its bit <i>ringly-shifted</i> to the left
	**/
	public static Long    ringOpLeft(Long    x, Byte len) {
		long
			byt = 64,
			inv = -1,
			tak = x & (inv << len),
			rem = x & ~tak;
		return  (rem << (byt-len)) | (tak >>> len);
	}
	// Aliases
	/** shorthand for <b>left-way</b> ring-operation based
	 *  @param  x   number
	 *  @param  len cycle length
	 *  @return given <i>x</i> with cycled <i>len</i>-bit to the left side
	 *  @see #ringOpLeft(Integer,Byte)
	**/
	public static Integer ringOpLeft(Integer x, Integer len) { return ringOpLeft(x,len.byteValue()); }
	/** shorthand for <b>left-way</b> ring-operation based
	 *  @param  x   number
	 *  @param  len cycle length
	 *  @return given <i>x</i> with cycled <i>len</i>-bit to the left side
	 *  @see #ringOpLeft(Long,Byte)
	**/
	public static Long    ringOpLeft(Long    x, Integer len) { return ringOpLeft(x,len.byteValue()); }
	
	
	/** 
	 *  shifts all bit to the right, rightmost [len] bits are moved to most significant position in order
	 *  @param x   the number
	 *  @param len how many bits that you want to take
	 *  @return number that have its bit <i>ringly-shifted</i> to the right
	**/
	public static Integer ringOpRight(Integer   x, Byte len) {
		int
			sgl =  1,
			byt = 32,
			tak = x & (sgl << len - sgl),
			rem = x & ~tak;
		return  (tak << (byt - len)) | (rem >>> len);
	}
	/** 
	 *  shifts all bit to the right, rightmost [len] bits are moved to most significant position in order
	 *  @param x   the number
	 *  @param len how many bits that you want to take
	 *  @return number that have its bit <i>ringly-shifted</i> to the right
	**/
	public static Long  ringOpRight(Long  x, Byte len) {
		long
			sgl =  1,
			byt = 64,
			tak = x & (sgl << len - sgl),
			rem = x & ~tak;
		return  (tak << (byt - len)) | (rem >>> len);
	}
	// Aliases
	/** shorthand for <b>right-way</b> ring-operation based
	 *  @param  x   number
	 *  @param  len cycle length
	 *  @return given <i>x</i> with cycled <i>len</i>-bit to the right side
	 *  @see #ringOpRight(Integer,Byte)
	**/
	public static Integer ringOpRight(Integer x, Integer  len) { return ringOpRight(x,len.byteValue()); }
	/** shorthand for <b>right-way</b> ring-operation based
	 *  @param  x   number
	 *  @param  len cycle length
	 *  @return given <i>x</i> with cycled <i>len</i>-bit to the right side
	 *  @see #ringOpRight(Long,Byte)
	 *  @see #ringOpRight(Number,Integer)
	**/
	public static Long    ringOpRight(Long    x, Integer  len) { return ringOpRight(x,len.byteValue()); }
	/** shorthand for <b>right-way</b> ring-operation based
	 *  @param  x   number
	 *  @param  len cycle length
	 *  @return given <i>x</i> with cycled <i>len</i>-bit to the right side
	 *  @see #ringOpRight(Long,Byte)
	 *  @see #ringOpRight(Long,Integer)
	**/
	public static Number  ringOpRight(Number  x, Integer  len) { return ringOpRight(x.longValue(),len.byteValue()); }
	
	public static byte inverseBit(byte x,int i) {
		byte b = readBit(x,i);
		return (byte)writeBit(x,i,b==0 ? 1 : 0);
	}
	public static short inverseBit(short x,int i) {
		byte b = readBit(x,i);
		return (short)writeBit(x,i,b==0 ? 1 : 0);
	}
	public static int inverseBit(int x,int i) {
		byte b = readBit(x,i);
		return writeBit(x,i,b==0 ? 1 : 0);
	}
	public static long inverseBit(long x,int i) {
		byte b = readBit(x,i);
		return writeBit(x,i,b==0 ? 1 : 0);
	}
	/**
	 *  turn the number into a bit form in string
	 *  @param x   the number
	 *  @param len how many bits that you want to take FROM RIGHT
	 *  @return number that converted into bit-string format
	**/
	public static String showBits(Number x, int len) {
		String toBuild = "";
		while(len-- > 0) {
			toBuild = toBuild + (x.longValue() >>> len & 1);
		}
		return toBuild;
	}
	/**
	 *  lengthy shorthand to directly convert the number into bits without specifying the limit
	 *  @param  x  the number
	 *  @return number that converted into bit-string format
	**/
	public static String showBits(Number x) {
		try {
			return showBits(x,(int)x.getClass().getDeclaredField("SIZE").get(null));
		} catch (Exception e) {
			return showBits(x,64);
		}
	}
	public static long bitConstruct(Boolean... bits) {
		return bitConstruct(Stream.of(bits).mapToInt(x->x?1:0).toArray());
	}
	public static long bitConstruct(int... bits) {
		byte[] nbit = new byte[bits.length];
		for(int i=0;i<bits.length;i++)
			nbit[i]=(byte)bits[i];
		return bitConstruct(nbit);
	}
	public static long bitConstruct(byte... bits) {
		return bitConstruct(bits[0],Arrays.copyOfRange(bits,1,bits.length-1));
	}
	private static long bitConstruct(long val, byte... bits) {
		return (bits.length<=1) ? (val<<1)|bits[0] :
			bitConstruct((val<<1)|bits[0],Arrays.copyOfRange(bits,1,bits.length-1));
	}
	/** Bit Operator Test
	 *  class test drive
	 *  @param argv arguments to be passed on.... It has no effect.
	**/
	public static void main(String[] argv) {
		//Class[]  nAry = new Class[]{Byte.class,Short.class,Integer.class,Long.class};
		Number[] vAry = new Number[]{(byte)1,(short)100,10000,1000000L};
		Number   x;
		try {
			Method[] m = new Method[]{
				Byte.class.getMethod("byteValue"),
				Short.class.getMethod("shortValue"),
				Integer.class.getMethod("intValue"),
				Long.class.getMethod("longValue"),
			};
		} catch (Exception e) {
		}
		System.out.println("\t bits: " + BitOperator.showBits(49));
		System.exit(-1);
		/*
		for(short i=0; i<vAry.length; i++) {
			x = vAry[i];
			System.out.println("Base value: " + x + " ("+x.getClass().getName()+")");
			System.out.println("\t bits: " + BitOperator.showBits(x));
			x = m[i].invoke(x,BitOperator.writeBit(x, 4, 1));
			System.out.println("\t bits: " + BitOperator.showBits(x));
			System.out.println(x + " left  ring 12 = " + BitOperator.ringOpLeft(x,12));
			System.out.println("\t bits: " + BitOperator.showBits(BitOperator.ringOpLeft(x,12)));
			System.out.println(x + " right ring 12 = " + BitOperator.ringOpRight(x,12));
			System.out.println("\t bits: " + BitOperator.showBits(BitOperator.ringOpRight(x,12)));
		} */
	}
}
