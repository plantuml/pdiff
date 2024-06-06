package com.pdiff.code;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class TranscoderImpl {

	private final CompressionZlib compression;
	private final AsciiEncoder urlEncoder;
	private final ArobaseStringCompressor2 stringCompressor;

	public TranscoderImpl(AsciiEncoder urlEncoder, ArobaseStringCompressor2 stringCompressor,
			CompressionZlib compression) {
		this.compression = compression;
		this.urlEncoder = urlEncoder;
		this.stringCompressor = stringCompressor;
	}

	public String decode(String code) throws IOException, DataFormatException {
		final byte compressedData[] = urlEncoder.decode(code);
		final ByteArray data = compression.decompress(compressedData);
		final String string;
		string = data.toUFT8String();
		return stringCompressor.decompress(string);
	}

}
