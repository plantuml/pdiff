package com.pdiff.humhash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SyllabexTest {

	@Test
	void testFirstSyllable() {
		assertEquals(0, Syllabex.toInt("ca"));
	}

	@Test
	void testSecondSyllable() {
		assertEquals(1, Syllabex.toInt("ce"));
	}

	@Test
	void testCiCocu() {
		assertEquals(2, Syllabex.toInt("ci"));
		assertEquals(3, Syllabex.toInt("co"));
		assertEquals(4, Syllabex.toInt("cu"));
	}

	@Test
	void testSecondConsonant() {
		assertEquals(5, Syllabex.toInt("ba"));
		assertEquals(6, Syllabex.toInt("be"));
	}

	@Test
	void testLastSyllable() {
		// z is index 16, u is index 4 => 16 * 5 + 4 = 84
		assertEquals(84, Syllabex.toInt("zu"));
	}

	@Test
	void testInvalidConsonant() {
		assertThrows(IllegalArgumentException.class, () -> Syllabex.toInt("ha"));
	}

	@Test
	void testInvalidVowel() {
		assertThrows(IllegalArgumentException.class, () -> Syllabex.toInt("cy"));
	}

	@Test
	void testToSyllableFirst() {
		assertEquals("ca", Syllabex.toSyllable(0));
	}

	@Test
	void testToSyllableSecond() {
		assertEquals("ce", Syllabex.toSyllable(1));
	}

	@Test
	void testToSyllableLast() {
		assertEquals("zu", Syllabex.toSyllable(84));
	}

	@Test
	void testToSyllableSecondConsonant() {
		assertEquals("ba", Syllabex.toSyllable(5));
	}

	@Test
	void testToSyllableNegative() {
		assertThrows(IllegalArgumentException.class, () -> Syllabex.toSyllable(-1));
	}

	@Test
	void testToSyllableTooLarge() {
		assertThrows(IllegalArgumentException.class, () -> Syllabex.toSyllable(85));
	}

	@Test
	void testRoundTrip() {
		for (int i = 0; i < 85; i++)
			assertEquals(i, Syllabex.toInt(Syllabex.toSyllable(i)));
	}

}
