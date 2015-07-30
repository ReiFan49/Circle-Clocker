package com.rfhkr.util;

import com.rfhkr.cc.errors.*;
import com.sun.istack.internal.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/04
 */
public final class FileMarshal {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	public static final byte VERSION_MAJOR = 1;
	public static final byte VERSION_MINOR = 0;
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static FileMarshal loadFromFile(String fn) throws Exception {
		return new FileMarshal(FileMode.FILE_LOAD,fn);
	}
	public static FileMarshal saveToFile  (String fn) throws Exception {
		return new FileMarshal(FileMode.FILE_SAVE,fn);
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	Pair<InputStream,OutputStream>                 fstr;
	Pair<BufferedInputStream,BufferedOutputStream> bffr;
	Pair<ObjectInputStream,ObjectOutputStream>     objr;
	FileMode fileMode;
	Throwable failedSwap = null;
	// ** ACCESSORS
	public boolean cannotSwap() { return Objects.isNull(fstr.get1st()); }
	public Throwable cannotSwapReason() { return failedSwap; }
	/** picks pair item using given class
	 * @param  p  pair object that needs to be checked
	 * @param  cc a class that used to choose/pick either c1 or c2 (c1 > c2, if same) element on pair p
	 * @return an item that matches either c1 or c2 class, with c1 as precedence and null as last resort
	 */
	@SuppressWarnings("unchecked")
	private <IS,OS,RS> RS getPairCond(Pair<IS,OS> p,Class<? super RS> cc) {
		return p.getElemCond(null,
		(c,a) -> cc.equals(a.getClass()),
		(c,b) -> cc.equals(b.getClass())
		);
	}
	private <FS> FS getFileStream(Class<FS> c) {
		return getPairCond(fstr,c);
	}
	private <FS> FS getBufferedStream(Class<FS> c) {
		return getPairCond(bffr,c);
	}
	private <FS> FS getObjectStream(Class<FS> c) {
		return getPairCond(objr,c);
	}
	private Class getDesiredClass(Class c1,Class c2) {
		switch(fileMode) {
			case FILE_SAVE: return c1;
			case FILE_LOAD: return c2;
			default: return null;
		}
	}
	public Object getFileStream() {
		return getFileStream(getDesiredClass(InputStream.class,OutputStream.class));
	}
	public Object getBufferedStream() {
		return getBufferedStream(getDesiredClass(BufferedInputStream.class, BufferedOutputStream.class));
	}
	public Object getObjectStream() {
		return getObjectStream(getDesiredClass(ObjectInputStream.class, ObjectOutputStream.class));
	}
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public FileMarshal swapMode() {
		if(cannotSwap())
			return this;
		switch(fileMode) {
			case FILE_LOAD: fileMode = FileMode.FILE_SAVE; break;
			case FILE_SAVE: fileMode = FileMode.FILE_LOAD; break;
			default: fileMode = null;
		}
		return this;
	}
	public void close() {
		try {
			switch(fileMode) {
				case FILE_LOAD: objr.get1st().close(); break;
				case FILE_SAVE: objr.get2nd().close(); break;
			}
		} catch (Exception e) {
			System.err.printf("%s: %s%n",e,e.getMessage());
		}
	}
	public <T> FileMarshal dump(T obj) {
		try {
			if(fileMode!=FileMode.FILE_SAVE) throw ReiException.invoke("File mode invalid");
			objr.get2nd().write(new byte[]{VERSION_MAJOR,VERSION_MINOR});
			objr.get2nd().writeObject(obj);
		} catch (Exception e) { e.printStackTrace(); }
		return this;
	}
	public FileMarshal dump(@NotNull Object... objs) {
		Arrays.stream(objs).forEach(obj->
			dump(obj.getClass().cast(obj))
		);
		return this;
	}
	public <T> T load() {
		try {
			if (fileMode != FileMode.FILE_LOAD)
				throw ReiException.invoke("File mode invalid");
			if(objr.get1st().available()<=0)
				throw new EOFException();
			byte[] v_ary = new byte[2];
			if(objr.get1st().read(v_ary)<2)
				throw ReiException.invoke("Version buffer check is less than 2 bytes ("+Arrays.toString(v_ary)+" )");
			if(!((v_ary[0]==VERSION_MAJOR)&&(v_ary[1]==VERSION_MINOR)))
				throw ReiException.invoke("Stream version mismatch (file:"+
				  Stream.of(v_ary).map(b->b.toString()).collect(Collectors.joining(",")) +
					")->(this:"+
					Stream.of(VERSION_MAJOR,VERSION_MINOR).map(b->b.toString()).collect(Collectors.joining(","))+")");
			T obj = (T)objr.get1st().readObject();
			return obj;
		} catch (Exception e) { System.err.println(e.getMessage()); }
		return null;
	}
	// <<END>> Instance Structure
	// Nested Classes
	private enum FileMode { FILE_NONE, FILE_SAVE, FILE_LOAD }
	// Constructors
	private FileMarshal(FileMode fm,String fn) throws Exception {
		try {
			if(fm==FileMode.FILE_SAVE)
				throw new Exception("requested");
			fstr = Pair.gen(new FileInputStream(fn),null);
			bffr = Pair.gen(new BufferedInputStream(fstr.get1st()),null);
			objr = Pair.gen(new ObjectInputStream(bffr.get1st()),null);
		} catch (Exception e) {
			System.err.println("Switching to save-only, because " + e);
			fstr = Pair.gen(null,new FileOutputStream(fn));
			bffr = Pair.gen(null,new BufferedOutputStream(fstr.get2nd()));
			objr = Pair.gen(null,new ObjectOutputStream(bffr.get2nd()));
			failedSwap = e;
		}
		switch(fm) {
			case FILE_SAVE:
			case FILE_LOAD:
				fileMode = fm;
				break;
			default:
				throw new RuntimeException("Unsupported Marshalling mode!");
		}
	}
	// Driver
}
