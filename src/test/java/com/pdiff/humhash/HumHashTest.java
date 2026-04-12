package com.pdiff.humhash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HumHashTest {

	@Test
	void testAllZeros() {
		// "cacaca-000-cacaca" => all syllables are 0, number is 0
		final HumHash humHash = HumHash.fromValue("cacaca-000-cacaca");
		assertEquals(0L, humHash.getHash());
	}

	@Test
	void testSameValueGivesSameHash() {
		final HumHash h1 = HumHash.fromValue("bavicu-123-terazo");
		final HumHash h2 = HumHash.fromValue("bavicu-123-terazo");
		assertEquals(h1.getHash(), h2.getHash());
	}

	@Test
	void testDifferentValuesGiveDifferentHash() {
		final HumHash h1 = HumHash.fromValue("bavicu-123-terazo");
		final HumHash h2 = HumHash.fromValue("bavicu-124-terazo");
		assertNotEquals(h1.getHash(), h2.getHash());
	}

	@Test
	void testHashIsPositive() {
		final HumHash humHash = HumHash.fromValue("bavicu-123-terazo");
		assertEquals(true, humHash.getHash() > 0);
	}

	@Test
	void testMaxValue() {
		// "zuzuzu-999-zuzuzu" => all syllables are 84, number is 999
		final HumHash humHash = HumHash.fromValue("zuzuzu-999-zuzuzu");
		assertEquals(true, humHash.getHash() > 0);
	}

	@Test
	void testInvalidFormatNoDash() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu123terazo"));
	}

	@Test
	void testInvalidFormatTooShortLeft() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavi-123-terazo"));
	}

	@Test
	void testInvalidFormatTooShortRight() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-123-tera"));
	}

	@Test
	void testInvalidFormatNumberTooShort() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-12-terazo"));
	}

	@Test
	void testInvalidSyllable() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("havicu-123-terazo"));
	}

	@Test
	void testToValueAllZeros() {
		final HumHash humHash = HumHash.fromValue("cacaca-000-cacaca");
		assertEquals("cacaca-000-cacaca", humHash.toValue());
	}

	@Test
	void testToValueMaxValue() {
		final HumHash humHash = HumHash.fromValue("zuzuzu-999-zuzuzu");
		assertEquals("zuzuzu-999-zuzuzu", humHash.toValue());
	}

	@Test
	void testRoundTrip() {
		final String value = "bavicu-123-terazo";
		assertEquals(value, HumHash.fromValue(value).toValue());
	}

	@Test
	void testRoundTripVariousValues() {
		final String[] values = { "cacace-001-cabace", "zubeda-500-kidofu", "lusami-042-novexu" };
		for (final String value : values)
			assertEquals(value, HumHash.fromValue(value).toValue());
	}

}
