package com.pdiff.humhash;

public class Syllabex {
	
	private static final String CONSONANTS = "cbdfgjklmnprstvxz";
	private static final String VOWELS = "aeiou";

	public static int toInt(String s) {
		final int consonant = CONSONANTS.indexOf(s.charAt(0));
		final int vowel = VOWELS.indexOf(s.charAt(1));
		if (consonant == -1 || vowel == -1)
			throw new IllegalArgumentException("Invalid syllable: " + s);
		return consonant * VOWELS.length() + vowel;
	}

	public static String toSyllable(int value) {
		if (value < 0 || value >= CONSONANTS.length() * VOWELS.length())
			throw new IllegalArgumentException("Invalid value: " + value);
		final char consonant = CONSONANTS.charAt(value / VOWELS.length());
		final char vowel = VOWELS.charAt(value % VOWELS.length());
		return "" + consonant + vowel;
	}

}
