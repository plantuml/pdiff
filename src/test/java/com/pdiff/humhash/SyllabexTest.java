package com.pdiff.humhash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SyllabexTest {

	@Test
	void testFirstSyllable() {
		assertEquals(0, Syllabex.toInt("ba"));
	}

	@Test
	void testSecondSyllable() {
		assertEquals(1, Syllabex.toInt("be"));
	}

	@Test
	void testBibobu() {
		assertEquals(2, Syllabex.toInt("bi"));
		assertEquals(3, Syllabex.toInt("bo"));
		assertEquals(4, Syllabex.toInt("bu"));
	}

	@Test
	void testSecondConsonant() {
		assertEquals(5, Syllabex.toInt("ca"));
		assertEquals(6, Syllabex.toInt("ce"));
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
		assertEquals("ba", Syllabex.toSyllable(0));
	}

	@Test
	void testToSyllableSecond() {
		assertEquals("be", Syllabex.toSyllable(1));
	}

	@Test
	void testToSyllableLast() {
		assertEquals("zu", Syllabex.toSyllable(84));
	}

	@Test
	void testToSyllableSecondConsonant() {
		assertEquals("ca", Syllabex.toSyllable(5));
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
