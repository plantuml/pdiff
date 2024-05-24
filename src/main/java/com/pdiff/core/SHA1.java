package com.pdiff.core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class SHA1 {

	public static String calculateSHA1(List<String> content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			final String contentString = content.stream().collect(Collectors.joining("\n"));
			final byte[] hash = digest.digest(contentString.getBytes());
			final BigInteger number = new BigInteger(1, hash);
			return number.toString(36);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
