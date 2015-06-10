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
    // <<END>> Class Structure
    // <BEGIN> Instance Structure
    // ** PROPERTIES
    private String fileName;
    // ** ACCESSORS
    // ** PREDICATES
    // ** INTERACTIONS
    // ** METHODS
    public String resolve(String fileName) {
        return Arrays.stream(new String[]{"core","assets",fileName})
                   .collect(Collectors.joining(File.separator));
    }
    public String toString() {return resolve(fileName);}
    // <<END>> Instance Structure
    // Constructors
    private PathResolver(String fileName) {
        this.fileName = fileName;
    }
}
