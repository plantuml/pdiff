package com.pdiff.humhash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HumHashTest {

	@Test
	void testAllZeros() {
		final HumHash humHash = HumHash.fromValue("cacaca-000-caca-00");
		assertEquals(0L, humHash.getHash());
	}

	@Test
	void testSameValueGivesSameHash() {
		final HumHash h1 = HumHash.fromValue("bavicu-123-tera-45");
		final HumHash h2 = HumHash.fromValue("bavicu-123-tera-45");
		assertEquals(h1.getHash(), h2.getHash());
	}

	@Test
	void testDifferentValuesGiveDifferentHash() {
		final HumHash h1 = HumHash.fromValue("bavicu-123-tera-45");
		final HumHash h2 = HumHash.fromValue("bavicu-124-tera-45");
		assertNotEquals(h1.getHash(), h2.getHash());
	}

	@Test
	void testHashIsPositive() {
		final HumHash humHash = HumHash.fromValue("bavicu-123-tera-45");
		assertEquals(true, humHash.getHash() > 0);
	}

	@Test
	void testMaxValue() {
		final HumHash humHash = HumHash.fromValue("zuzuzu-999-zuzu-99");
		assertEquals(true, humHash.getHash() > 0);
	}

	@Test
	void testInvalidFormatNoDash() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu123tera45"));
	}

	@Test
	void testInvalidFormatTooShortPart1() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavi-123-tera-45"));
	}

	@Test
	void testInvalidFormatTooShortPart2() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-123-te-45"));
	}

	@Test
	void testInvalidFormatNumber1TooShort() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-12-tera-45"));
	}

	@Test
	void testInvalidFormatNumber2TooShort() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-123-tera-4"));
	}

	@Test
	void testInvalidSyllable() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("havicu-123-tera-45"));
	}

	@Test
	void testToValueAllZeros() {
		final HumHash humHash = HumHash.fromValue("cacaca-000-caca-00");
		assertEquals("cacaca-000-caca-00", humHash.toValue());
	}

	@Test
	void testToValueMaxValue() {
		final HumHash humHash = HumHash.fromValue("zuzuzu-999-zuzu-99");
		assertEquals("zuzuzu-999-zuzu-99", humHash.toValue());
	}

	@Test
	void testRoundTrip() {
		final String value = "bavicu-123-tera-45";
		assertEquals(value, HumHash.fromValue(value).toValue());
	}

	@Test
	void testRoundTripVariousValues() {
		final String[] values = { "cacace-001-caba-07", "zubeda-500-kido-42", "lusami-042-nove-99" };
		for (final String value : values)
			assertEquals(value, HumHash.fromValue(value).toValue());
	}
	
	@Test
	void test1() {
		final HumHash humHash = HumHash.fromContent(Arrays.asList("this is", "some data"));
		assertEquals("jaloma-340-kini-47", humHash.toValue());
	}


}
