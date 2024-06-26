package com.pdiff.code;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ByteArray {

	private final byte data[];
	private final int length;

	private ByteArray(byte data[], int length) {
		this.data = data;
		this.length = length;
	}

	public static ByteArray from(byte[] input) {
		return new ByteArray(input, input.length);
	}

	public String toUFT8String() throws UnsupportedEncodingException {
		return new String(data, 0, length, UTF_8);
	}

	public int getByteAt(int i) {
		return data[i];
	}

	public int length() {
		return length;
	}

}
