package com.pdiff.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class MagicOutput {

	private final String[] livingPart;

	// Sauvegarde des flux d'origine
	private PrintStream originalOut = System.out;
	private PrintStream originalErr = System.err;

	public MagicOutput(int livingLines) {

		// Initialiser la console Jansi
		AnsiConsole.systemInstall();

		final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		// Redirection des flux
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));

		this.livingPart = new String[livingLines];
		for (int i = 0; i < livingLines; i++)
			this.livingPart[i] = "";

		for (String s : livingPart)
			originalOut.println(s);

	}

	private void disableRegularOutput() {

	}
	
	public void restoreOutput() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	public void clearLivingPart() {

	}

	public synchronized void appendTop(String top) {
		final int up = livingPart.length;
		for (int i = 0; i < up; i++)
			originalOut.print(Ansi.ansi().cursorUpLine());
		originalOut.print(Ansi.ansi().eraseLine());
		originalOut.println(top);
		for (String s : this.livingPart) {
			originalOut.print(Ansi.ansi().eraseLine());
			originalOut.println(s);
		}

	}

	public synchronized void updateLivingPart(int num, String s) {
		this.livingPart[num] = s;
		final int up = livingPart.length - num;
		for (int i = 0; i < up; i++)
			originalOut.print(Ansi.ansi().cursorUpLine());
		originalOut.print(Ansi.ansi().eraseLine());
		originalOut.println(s);
		final int down = up - 1;
		for (int i = 0; i < down; i++)
			originalOut.print(Ansi.ansi().cursorDownLine());

	}

}
