package com.pdiff.humhash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HumHashTest {

	@Test
	void testAllZeros() {
		final HumHash humHash = HumHash.fromValue("cacaca-00-caca000");
		assertEquals(0L, humHash.getHash());
	}

	@Test
	void testSameValueGivesSameHash() {
		final HumHash h1 = HumHash.fromValue("bavicu-12-tera345");
		final HumHash h2 = HumHash.fromValue("bavicu-12-tera345");
		assertEquals(h1.getHash(), h2.getHash());
	}

	@Test
	void testDifferentValuesGiveDifferentHash() {
		final HumHash h1 = HumHash.fromValue("bavicu-12-tera345");
		final HumHash h2 = HumHash.fromValue("bavicu-13-tera345");
		assertNotEquals(h1.getHash(), h2.getHash());
	}

	@Test
	void testHashIsPositive() {
		final HumHash humHash = HumHash.fromValue("bavicu-12-tera345");
		assertEquals(true, humHash.getHash() > 0);
	}

	@Test
	void testMaxValue() {
		final HumHash humHash = HumHash.fromValue("zuzuzu-99-zuzu999");
		assertEquals(true, humHash.getHash() > 0);
	}

	@Test
	void testInvalidFormatNoDash() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu12tera345"));
	}

	@Test
	void testInvalidFormatTooShortPart1() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavi-12-tera345"));
	}

	@Test
	void testInvalidFormatTooShortPart2() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-12-te345"));
	}

	@Test
	void testInvalidFormatNumber1TooShort() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("bavicu-1-tera345"));
	}

	@Test
	void testInvalidSyllable() {
		assertThrows(IllegalArgumentException.class, () -> HumHash.fromValue("havicu-12-tera345"));
	}

	@Test
	void testToValueAllZeros() {
		final HumHash humHash = HumHash.fromValue("cacaca-00-caca000");
		assertEquals("cacaca-00-caca000", humHash.toValue());
	}

	@Test
	void testToValueMaxValue() {
		final HumHash humHash = HumHash.fromValue("zuzuzu-99-zuzu999");
		assertEquals("zuzuzu-99-zuzu999", humHash.toValue());
	}

	@Test
	void testRoundTrip() {
		final String value = "bavicu-12-tera345";
		assertEquals(value, HumHash.fromValue(value).toValue());
	}

	@Test
	void testRoundTripVariousValues() {
		final String[] values = { "cacace-01-caba007", "zubeda-50-kido042", "lusami-04-nove999" };
		for (final String value : values)
			assertEquals(value, HumHash.fromValue(value).toValue());
	}
	
	@Test
	void test1() {
		final HumHash humHash = HumHash.fromContent(Arrays.asList("this is", "some data"));
		assertEquals("jaloma-34-coge747", humHash.toValue());
	}



}
