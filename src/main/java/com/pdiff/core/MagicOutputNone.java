package com.pdiff.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MagicOutputNone {
	
	// Sauvegarde des flux d'origine
	private PrintStream originalOut = System.out;
	private PrintStream originalErr = System.err;


	public MagicOutputNone(int livingLines) {
		final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		// Redirection des flux
		// System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));


	}

	private void disableRegularOutput() {

	}

	public void restoreOutput() {
	}

	public void clearLivingPart() {

	}

	public synchronized void appendTop(String top) {
		System.out.println(top);
	}

	public synchronized void updateLivingPart(int num, String s) {
		System.out.println(s);
	}

}
