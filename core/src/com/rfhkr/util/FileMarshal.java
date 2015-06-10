package com.rfhkr.util;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;

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
	/*
		try {
			Arrays.stream(objs).peek(o-> {
				try {
				objr.writeObject(o.getClass().cast(o));
				} catch (Exception e) {
					System.err.println(e);
				} finally {
				}
			});
			objr.close();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
		} */
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	Pair<InputStream,OutputStream>                 fstr;
	Pair<BufferedInputStream,BufferedOutputStream> bffr;
	Pair<ObjectInputStream,ObjectOutputStream>     objr;
	FileMode fileMode;
	// ** ACCESSORS
	/** picks pair item using given class
	 * @param  p  pair object that needs to be checked
	 * @param  cc a class that used to choose/pick either c1 or c2 (c1 > c2, if same) element on pair p
	 * @return an item that matches either c1 or c2 class, with c1 as precedence and null as last resort
	 */
	@SuppressWarnings("unchecked")
	private <IS,OS,RS> RS getPairCond(Pair<IS,OS> p,Class<? super RS> cc) {
		return p.getElemCond(null,
			(c,a)->cc.equals(a.getClass()),
			(c,b)->cc.equals(b.getClass())
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
		switch(fileMode) {
			case FILE_LOAD: fileMode = FileMode.FILE_SAVE; break;
			case FILE_SAVE: fileMode = FileMode.FILE_LOAD; break;
			default: fileMode = null;
		}
		return this;
	}
	// <<END>> Instance Structure
	// Constructors
	private FileMarshal(FileMode fm,String fn) throws Exception {
		fstr = Pair.gen(new FileInputStream(fn),new FileOutputStream(fn));
		bffr = Pair.gen(new BufferedInputStream(fstr.get1st()),new BufferedOutputStream(fstr.get2nd()));
		objr = Pair.gen(new ObjectInputStream(bffr.get1st()),new ObjectOutputStream(bffr.get2nd()));
		switch(fm) {
			case FILE_SAVE:
			case FILE_LOAD:
				fileMode = fm;
				break;
			default:
				throw new Exception("Unsupported Marshalling mode!");
		}
	}
	// Nested Classes
	private enum FileMode { FILE_NONE, FILE_SAVE, FILE_LOAD }
	// Driver
}
