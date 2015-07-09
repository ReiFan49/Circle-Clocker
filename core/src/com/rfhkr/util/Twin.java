/*\
|*| Nama / NIM : Irfan Gunawan
|*| Tanggal    : 06 April 2015
\*/

package com.rfhkr.util;

/** manages tuple of two elements of the same class
 *  @param <T> class target
 *  @author  Rei Hakurei
 *  @version 2015/04/06
**/
public class Twin<T> extends Pair<T,T> {
	// Class Properties
	// Instance Properties
	/** first item of the tuple **/
	private T first;
	/** second item of the tuple **/
	private T second;
	/** pair referrer, to safe memory whenever swapping happens **/
	private Twin<T> referrer;
	// Class Accessors
	// Instance Accessors
	// Class Methods
	/** generate pair instead using constructor (alias)
	 *  @param  x   first item
	 *  @param  y   second item
	 *  @return new pair
	**/
	@SuppressWarnings("unchecked")
	public static <E> Twin<E> set(E x,E y) {
		return new Twin<>(x,y);
	}
	public static Twin<?> gen(Object x,Object y) {
		throw new IllegalAccessError("use Twin.set to create a new Twin object quickly!");
	}
	// Instance Methods
	/** swaps tuple by creating the new class if it has not been referred before.
	 *  @param  clone  decides that the reference will be made and no actual cloning performed or not (if <b>false</b>)
	 *  @return swapped pair, either have the reference to the current item or not
	 **/
	@Override
	@SuppressWarnings("unchecked")
	public Twin<T> swapPair(boolean clone) {
		// defines swapped pair object, returns the referrer if it comes from swapping -- unless specified to not to clone
		return !clone?((referrer==null)?new Twin<>(getY(),getX(),this):referrer):new Twin<>(getY(),getX());
	}
	/** swaps pair element without specifying exclusion of the referrer of the cloned item
	 *  @return swapped pair if its purely made by basic construction<br>
	 *          or the old pair reference if the pair was made via swapping.
	 **/
	public Twin<T> swapPair() { return swapPair(false); }
	// Constructors
	/** constructs a tuple of two element of the same class
	 *  @param  x tuple item
	**/
	public Twin(T x) { super(x,x); }
	/** constructs a tuple of two element of the same class
	 *  @param  x tuple item
	 *  @param  y tuple item
	**/
	public Twin(T x,T y) {
		super(x,y);
	}
	/** silently constructs a tuple of two element with reference to the other tuple object
	 *  @param  x tuple item
	 *  @param  y tuple item
	 *  @param  r tuple referrer
	**/
	@SuppressWarnings("unchecked")
	protected Twin(T x,T y,Pair<T,T> r) {
		super(x,y,r);
	}
	// Driver
	@SuppressWarnings("unchecked")
	public static void main(String[] argv) {
		Pair<Integer,String> p = new Pair(49,"Rei Hakurei");
		Twin<Integer>        t = new Twin(4,9);
		System.out.println(p);
		System.out.println(t);
		System.out.println(p.swapPair());
		System.out.println(t.swapPair());
		System.out.println(p.swapPair().swapPair());
		System.out.println(p.swapPair().swapPair().equals(p));
		System.out.println(t.swapPair().swapPair());
		System.out.println(t.swapPair().swapPair().equals(t));
	}
}
