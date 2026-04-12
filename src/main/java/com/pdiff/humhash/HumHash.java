package com.pdiff.humhash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class HumHash {

	private final long hash;

	private HumHash(long hash) {
		this.hash = hash;
	}

	public static HumHash fromValue(String value) {
		final String[] parts = value.split("-");
		if (parts.length != 4)
			throw new IllegalArgumentException("Invalid format: " + value);

		final String part1 = parts[0];
		final int number1 = Integer.parseInt(parts[1]);
		final String part2 = parts[2];
		final int number2 = Integer.parseInt(parts[3]);

		if (part1.length() != 6 || parts[1].length() != 3 || part2.length() != 4 || parts[3].length() != 2)
			throw new IllegalArgumentException("Invalid format: " + value);

		long result = 0;
		result = result * 85 + Syllabex.toInt(part1.substring(0, 2));
		result = result * 85 + Syllabex.toInt(part1.substring(2, 4));
		result = result * 85 + Syllabex.toInt(part1.substring(4, 6));
		result = result * 1000 + number1;
		result = result * 85 + Syllabex.toInt(part2.substring(0, 2));
		result = result * 85 + Syllabex.toInt(part2.substring(2, 4));
		result = result * 100 + number2;

		return new HumHash(result);
	}
	
	
	public static HumHash fromContent(List<String> content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final String contentString = content.stream().collect(Collectors.joining("\n"));
			final byte[] hash = digest.digest(contentString.getBytes());
			// Take the first 8 bytes as a positive long
			long value = 0;
			for (int i = 0; i < 8; i++)
				value = (value << 8) | (hash[i] & 0xFF);
			// Ensure positive and fit within the format range (85^5 * 1000 * 100)
			value = Long.remainderUnsigned(value, 85L * 85 * 85 * 1000 * 85 * 85 * 100);
			return new HumHash(value);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}


	public long getHash() {
		return hash;
	}

	public String toValue() {
		long remaining = hash;

		final int number2 = (int) (remaining % 100);
		remaining /= 100;
		final String s5 = Syllabex.toSyllable((int) (remaining % 85));
		remaining /= 85;
		final String s4 = Syllabex.toSyllable((int) (remaining % 85));
		remaining /= 85;

		final int number1 = (int) (remaining % 1000);
		remaining /= 1000;

		final String s3 = Syllabex.toSyllable((int) (remaining % 85));
		remaining /= 85;
		final String s2 = Syllabex.toSyllable((int) (remaining % 85));
		remaining /= 85;
		final String s1 = Syllabex.toSyllable((int) (remaining % 85));

		return s1 + s2 + s3 + "-" + String.format("%03d", number1) + "-" + s4 + s5 + "-" + String.format("%02d", number2);
	}
}
