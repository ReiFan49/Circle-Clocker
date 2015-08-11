package com.rfhkr.util;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * @author Rei_Fan49
 * @since 2015/05/23
 */
public class PathResolver {
	// <BEGIN> Class Structure
	// ** PROPERTIES
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	public static PathResolver at(String fn) { return new PathResolver(fn); }
	public static PathResolver at(File fn)   { return new PathResolver(fn.getAbsolutePath()); }
	public static PathResolver from(String cp) {
		String[] buff = cp.split(File.separator+File.separator);
		return PathResolver.at(buff[buff.length-1]).build(Arrays.copyOf(buff,buff.length-1));
	}
	// <<END>> Class Structure
	// <BEGIN> Instance Structure
	// ** PROPERTIES
	private String fileName;
	private String[] builder = new String[0];
	// ** ACCESSORS
	// ** PREDICATES
	// ** INTERACTIONS
	// ** METHODS
	private String[] resolveAll() {
		String[] constructable = Arrays.copyOf(builder,builder.length+1);
		constructable[builder.length] = fileName;
		return constructable;
	}
	public PathResolver rep(String fn) { this.fileName = fn; return this; }
	public PathResolver build(String... builder) { this.builder=builder; return this; }
	public String resolve() {
		return Arrays.stream(resolveAll())
			 .collect(Collectors.joining(File.separator));
	}
	public String toString() {return resolve();}
	// <<END>> Instance Structure
	// Constructors
	private PathResolver(String fileName) {
			this.fileName = fileName;
	}
}
