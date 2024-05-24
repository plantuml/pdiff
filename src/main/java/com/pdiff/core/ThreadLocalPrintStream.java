package com.pdiff.core;

import java.io.OutputStream;
import java.io.PrintStream;

public class ThreadLocalPrintStream extends PrintStream {

	public ThreadLocalPrintStream(OutputStream out) {
		super(out);
	}

	public static void main(String[] args) {
		ThreadLocalPrintStream cut = new ThreadLocalPrintStream(System.out);
		System.setOut(cut);
		cut.println("OUTPUT0");
	}

}
