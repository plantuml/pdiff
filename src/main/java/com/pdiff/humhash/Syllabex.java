package com.pdiff.humhash;

public class Syllabex {
	
	// Consonant = c, b, d, f, g, j, k, l, m, n, p, r, s, t, v, x, z
	// Vowel = a, e, i, o, u

	private static final String CONSONANTS = "cbdfgjklmnprstvxz";
	private static final String VOWELS = "aeiou";

	public static int toInt(String s) {
		final int consonant = CONSONANTS.indexOf(s.charAt(0));
		final int vowel = VOWELS.indexOf(s.charAt(1));
		if (consonant == -1 || vowel == -1)
			throw new IllegalArgumentException("Invalid syllable: " + s);
		return consonant * VOWELS.length() + vowel;
	}


}
