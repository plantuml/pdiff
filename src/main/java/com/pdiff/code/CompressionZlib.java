package com.pdiff.code;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.pdiff.code.deflate.ByteBitInputStream;
import com.pdiff.code.deflate.Decompressor;


public class CompressionZlib {

	public ByteArray decompress(byte[] input) throws IOException, DataFormatException {
		final byte padded[] = new byte[input.length + 256];
		System.arraycopy(input, 0, padded, 0, input.length);

		final ByteBitInputStream inputStream = new ByteBitInputStream(new ByteArrayInputStream(padded));
		return ByteArray.from(Decompressor.decompress(inputStream));
	}

}
