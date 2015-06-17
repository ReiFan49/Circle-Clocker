package com.rfhkr.cc.io;

import com.rfhkr.util.*;

import java.io.*;

/**
 * @author Rei_Fan49
 * @since 2015/06/10
 */
public interface FileFormatReader<FileClass> {
	// Constant Fields
	// Abstract Methods
	FileClass parse(File f);
	// Pre-defined Methods
	default FileClass parse(String fn) { return parse(new File(fn)); }
	default FileClass parse(Object strfn) { return parse(strfn.toString()); }
}
