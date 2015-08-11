package com.rfhkr.util;

import java.util.*;

/**
  @author    Rei Hakurei
  @version   1.1
**/

public class Enumerable<T> implements Iterable<T>, java.io.Serializable {
	// Class Properties
	// Properties
	private T[] data;
	private int index = 0;
	private boolean loop = true;
	// Constructor
	/**
		creates Enumerable object of one type, omitting the loop parameter (default to true)
		@param ary array entry to be enumerated through
	**/
	public Enumerable(T[] ary) {
		this(ary,true);
	}
	/**
		creates Enumerable object of one type, whether allows automatic-looping or not for the enumeration itself.
		@param ary array entry to be enumerated through
		@param loop automatic looping flag.
	**/
	public Enumerable(T[] ary, boolean loop) {
		data = ary;
		this.loop = loop;
	}
	// Class Attribute Accessors
	// Attribute Readers
	/**
		obtains the current item from current enumerator state.
		@return item on current index
	**/
	public T getCurrent() { return data[index]; }
	/**
		obtains the item at <i>i</i>th index from current enumerator state.
		@param cueIndex index the item that need to be returned
		@return item on <i>cueIndex</i> index
	**/
	public T getCue(int cueIndex) {
		// get current item
		return data[cueIndex];
	}
	/**
		obtains the current index of the enumerator
		@return current enumerator index
	**/
	public int getIndex() { return index; }
	// Attribute Writers
	/*\ <void> Enumerable setCue(obj)
	|*| Arguments:
	|*| - <Variant> obj: object to replace
	|*|
	|*| Replaces the current enumerator object with the new one.
	\*/
	/**
		replaces the current index object from the enumerator with the specified one
		@param obj Object to replace the data in current index
	**/
	public void setCue(T obj){
		// set current item
		data[index] = obj;
	}
	// Class Methods
	// Methods
	/**
		Check if the enumerator is already at the start of the enumerable data,
		neccessary to perform &lt;do-while&gt; loop breaking by using this.
		@return state of the enumerator -- true if the index on its head
	**/
	public boolean firstCue() {
		// check if cue is asking head
		return index == 0;
	}
	/**
		Check if the enumerator is already at the end of the enumerable data,
		neccessary to perform &lt;while-not&gt; loop breaking by using this.
		@return state of the enumerator -- true if the index on its tail
	**/
	public boolean endCue() {
		// check if cue is asking tail
		return index >= data.length-1;
	}
	/**
		Resets the enumerator index to the first one.
	**/
	public void    resetCue() {
		index = 0;
	}
	/**
		Advances the current enumerator index to the next one, loop if neccessary
		@throws ArrayIndexOutOfBoundsException if the loop flag of the enumerator is disabled
		  and the enumerator already reaches its end, the out of bounds exception will be thrown
		  indicates it requires manual enumerator reset/loop.
	**/
	public void    nextCue() throws ArrayIndexOutOfBoundsException {
		// enumerate to next item
		if(loop) {
			index = (++index) % data.length; 
		} else {
			if (endCue()) {
				index = data.length-1;
				throw new ArrayIndexOutOfBoundsException("Enumerable object is not looping!");
			} else {
				index++;
			}
		}
	}
	/**
		iterates all item from the enumerator.
		useful if you want to enumerate through without modifying the content.
		@since  1.1
		@return iterable item containing the current object
	**/
	public Iterator<T> iterateAll() { return Arrays.asList(data).iterator(); }
	/**
		alias of iterateAll
		@see #iterateAll
		@since  1.1
	**/
	public Iterator<T> iterator()   { return Arrays.asList(data).iterator(); }
	// Class Tester
	/**
		tests the way this class works.
		@param argv array of integers to be tested through
	**/
	public static void main(String[] argv) {
		Enumerable<Integer> eInt = new Enumerable<Integer>((argv.length==0) ? new Integer[]{1,2,3,4,5} : new Integer[argv.length]);
		// Parse the given arguments
		for(int i=0;i<argv.length;i++) {
			eInt.setCue(Integer.parseInt(argv[i]));
			eInt.nextCue();
		}
		// Perform do-while loop on enumerator, enumerates until loops back to start
		do {
			eInt.setCue(eInt.getCurrent()*20);
			System.out.println(eInt.getCurrent());
			eInt.setCue(eInt.getCurrent()/25);
			eInt.nextCue();
		} while(!eInt.firstCue());
		// Perform enhanced-for-each loop on enumerator syntax.
		for(Integer i : eInt) {
			System.out.println(i);
		}
	}
}
