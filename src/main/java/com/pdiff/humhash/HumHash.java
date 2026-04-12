package com.pdiff.humhash;

public class HumHash {

	private final long hash;

	private HumHash(long hash) {
		this.hash = hash;
	}

	public static HumHash fromValue(String value) {
		final String[] parts = value.split("-");
		if (parts.length != 3)
			throw new IllegalArgumentException("Invalid format: " + value);

		final String left = parts[0];
		final int number = Integer.parseInt(parts[1]);
		final String right = parts[2];

		if (left.length() != 6 || right.length() != 6 || parts[1].length() != 3)
			throw new IllegalArgumentException("Invalid format: " + value);

		long result = 0;
		result = result * 85 + Syllabex.toInt(left.substring(0, 2));
		result = result * 85 + Syllabex.toInt(left.substring(2, 4));
		result = result * 85 + Syllabex.toInt(left.substring(4, 6));
		result = result * 1000 + number;
		result = result * 85 + Syllabex.toInt(right.substring(0, 2));
		result = result * 85 + Syllabex.toInt(right.substring(2, 4));
		result = result * 85 + Syllabex.toInt(right.substring(4, 6));

		return new HumHash(result);
	}

	public long getHash() {
		return hash;
	}
}
