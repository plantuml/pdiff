package com.pdiff.humhash;

public class Syllabex {
	
	// Consonant = C, B, D, F, G, J, K, L, M, N, P, R, S, T, V, X, Z
	// Vowel = A, E, I, O, U

	private static final String CONSONANTS = "CBDFGJKLMNPRSTVXZ";
	private static final String VOWELS = "AEIOU";

	public static int toInt(String s) {
		final String upper = s.toUpperCase();
		final int consonant = CONSONANTS.indexOf(upper.charAt(0));
		final int vowel = VOWELS.indexOf(upper.charAt(1));
		if (consonant == -1 || vowel == -1)
			throw new IllegalArgumentException("Invalid syllable: " + s);
		return consonant * VOWELS.length() + vowel;
	}


}
