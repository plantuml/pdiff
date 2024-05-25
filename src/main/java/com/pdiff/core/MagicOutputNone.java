package com.pdiff.core;

public class MagicOutputNone {

	public MagicOutputNone(int livingLines) {

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
