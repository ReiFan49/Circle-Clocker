package com.rfhkr.util;
import com.sun.istack.internal.*;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.function.*;

/** manages tuple of two elements
 *  @param   <T> first element class
 *  @param   <U> second element class
 *  @author  Rei Hakurei
 *  @version 2015/04/06
**/
public class Pair<T,U> implements Iterable<Object>, Serializable {
	// Class Properties
	// Instance Properties
	/** first item of the tuple **/
	private T first;
	/** second item of the tuple **/
	private U second;
	/** pair referrer, to safe memory whenever swapping happens **/
	private Pair<U,T> referrer;
	/** list of items in tuple **/
	private Object[]   objItems;
	/** list of classes in tuple **/
	private Class<?>[] objClasses;
	// Class Accessors
	// Instance Accessors
	/** retrieves the first object in the tuple
	 *  @return first item
	**/
	public T         getFirst  ()       {return first ;}
	/** retrieves the second object in the tuple
	 *  @return second item
	**/
	public U         getSecond ()       {return second;}
	/** retrieves classes of the object in the tuple
	 *  @return array of class representing each element in tuple
	**/
	public Class[]   getClasses()       {return objClasses;}
	/** change the item in the first index
	 *  @param  x set first element
	 *  @return self-reference
	**/
	public Pair<T,U> setFirst  (T x)    {this.first =x; return this.updateCoreData();}
	/** change the item in the second index
	 *  @param  x set second element
	 *  @return self-reference
	**/
	public Pair<T,U> setSecond (U x)    {this.second=x; return this.updateCoreData();}
	/** change any item in the tuple
	 *  @param  x set first element
	 *  @param  y set second element
	 *  @return self-reference
	**/
	public Pair<T,U> setBoth   (T x,U y){return setFirst(x).setSecond(y);}
	/* ALIASES */
	public T         getX    ()     { return getFirst (); }
	public T         get1st  ()     { return getFirst (); }
	public T         getLeft ()     { return getFirst (); }
	public U         getY    ()     { return getSecond(); }
	public U         get2nd  ()     { return getSecond(); }
	public U         getRight()     { return getSecond(); }
	public Pair<T,U> setX    (T x)  { return setFirst (x); }
	public Pair<T,U> set1st  (T x)  { return setFirst (x); }
	public Pair<T,U> setLeft (T x)  { return setFirst (x); }
	public Pair<T,U> setY    (U x)  { return setSecond(x); }
	public Pair<T,U> set2nd  (U x)  { return setSecond(x); }
	public Pair<T,U> setRight(U x)  { return setSecond(x); }
	// Class Methods
	/** generates tuple of summable elements that determine the next 2nd index item in the tuple
	 *  using fibonacci sequence
	 *  @param  ops  how many operation performed<br>
	 *               zero operation only returns the base of the fibonacci, which is [(1,1)].<br>
	 *               single operation only sums the zero operation pair item, which returns [(1,1),(1,<b>2</b>)]<br>
	 *               double operation only sums the zero-to-first operation pair item, which returns [(1,1),(1,2),(2,<b>3</b>)]<br>
	 *               and so on...
	 *  @return list of tuple of summable fibonacci sequences
	**/
	public static List<Pair<Integer,Integer>> generateFibonacci(int ops) {
		// prepare the actual list and the last pair to be added later on
		List<Pair<Integer,Integer>> li = new ArrayList<>();
		Pair<Integer,Integer> lp = new Pair<>(1,1);
		// if the operation permitted still higher than 0, then keep calculating over.
		while(--ops > 0) {
			li.add(lp);
			lp = Pair.gen(lp.get2nd(), lp.get1st() + lp.get2nd());
		}
		// add the last pair to the list, to ensure the list is not empty
		li.add(lp);
		return li;
	}
	/** generate pair instead using constructor (alias)
	 *  @param  x   first item
	 *  @param  y   second item
	 *  @return new pair
	**/
	public static <X,Y> Pair<X,Y> gen(X x,Y y) {
		return new Pair<>(x,y);
	}
	// Instance Methods
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o instanceof Pair) {
			Pair p = (Pair)o;
			if(first  !=null ? !first .equals(p.get1st()) : p.get1st()!=null) return false;
			if(second !=null ? !second.equals(p.get2nd()) : p.get2nd()!=null) return false;
			return true;
		}
		return false;
	}
	public int hashCode() {
		return Integer.reverseBytes(first.hashCode()) ^ second.hashCode();
	}
	/** updates the core data of the tuple, performed for every data updates in the tuple, for iteration purpose
	 *  @return self-reference
	**/
	private Pair<T,U> updateCoreData() {
		objItems      = (objItems  ==null)?(new Object[2]):objItems;
		objItems[0]   = get1st();
		objItems[1]   = get2nd();
		
		objClasses    = (objClasses==null)?(new Class [2]):objClasses;
		for(int i=0;i<objItems.length;i++) {
			objClasses[i] = (objItems[i] == null) ? Object.class : objItems[i].getClass();
		}
		
		return this;
	}
	/** turns the tuple into an iterable item
	 *  @return iterable tuple
	**/
	public Iterator<Object> iterator()   {
		updateCoreData();
		Object[] iterableItem = new Object[objItems.length];
		for(int i=0;i<objItems.length;i++) {
			iterableItem[i] = objClasses[i].cast(objItems[i]);
		}
		return Arrays.asList(iterableItem).iterator();
	}
	/** swaps pair element including the class by creating the new class if it has not been referred before.
	 *  @param  clone  decides that the reference will be made and no actual cloning performed or not (if <b>false</b>)
	 *  @return swapped pair, either have the reference to the current item or not
	**/
	@SuppressWarnings("unchecked")
	public Pair<U,T> swapPair(boolean clone) {
		// defines swapped pair object, returns the referrer if it comes from swapping -- unless specified to not to clone
		return !clone?((referrer==null)?new Pair(getY(),getX(),this):referrer):new Pair(getY(),getX());
	}
	/** swaps pair element without specifying exclusion of the referrer of the cloned item
	 *  @return swapped pair if its purely made by basic construction<br>
	 *          or the old pair reference if the pair was made via swapping.
	**/
	public Pair<U,T> swapPair() { return swapPair(false); }

	/** picks a pair element using conditional selection
	 * @since 2015/06/04
	 * @param cc value to compare with both element
	 * @param c1 first condition to be checked, if true returns #getX
	 * @param c2 second condition to be checked, either true or null returns #getY,
	 *           the null in here means only single condition check, either returns #getX or #getY without leaving any
	 *           null.
	 * @param <R> target return type.
	 * @return value that matches earlier condition, or null.
	 */
	@SuppressWarnings("unchecked")
	public <R> R getElemCond(R cc,@NotNull BiPredicate<R,T> c1,@Nullable BiPredicate<R,U> c2) {
		return c1.test(cc,first) ?
			(R)first : ((Objects.isNull(c2)||c2.test(cc,second)) ?
				(R)second : null);
	}
	/** turns the pair tuple into a twin
	 *  @since  2015/04/08
	 *  @throws IllegalArgumentException if the pair is not having a same type
	 *  @return cloned tuple of the same element that refers this object
	**/
	@SuppressWarnings("unchecked")
	public Twin<T> toTwin() throws IllegalArgumentException {
		if (!objClasses[0].equals(objClasses[1]))
			throw new IllegalArgumentException("Type mismatch for inner-conversion to Twin");
		return new Twin<>((T)getX(),(T)getY(),(Pair<T,T>)this);
	}
	/** converts the tuple into a string
	 *  @return string format of the tuple
	**/
	public String toString() { return "("+getX()+", "+getY()+")"; }
	// Constructors
	/** constructs a tuple of two element with possibility of having a different class
	 *  @param  x tuple item
	 *  @param  y tuple item
	**/
	public Pair(T x,U y) {
		this.first = x; this.second = y;
		updateCoreData();
	}
	/** silently constructs a tuple of two element with reference to the other tuple object
	 *  @param  x tuple item
	 *  @param  y tuple item
	 *  @param  r tuple referrer
	**/
	@SuppressWarnings("unchecked")
	protected Pair(T x,U y,Pair<U,T> r) {
		this(x,y);
		this.referrer = r;
	}
	// Driver
	public static void main(String[] argv) {
		Pair<Number,String> p = new Pair<>(49,"Rei Hakurei");
		System.out.println(p);
		System.out.println(p.swapPair());
		System.out.println(p.swapPair().swapPair());
		System.out.println(p.swapPair().swapPair().equals(p));
		System.out.println(Pair.generateFibonacci(5));
		System.out.println(Pair.generateFibonacci(10));
		System.out.println(Pair.generateFibonacci(20));
	}
}
