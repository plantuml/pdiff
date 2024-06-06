
package com.pdiff.code;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class TranscoderSmart {

	private final TranscoderImpl zlib = new TranscoderImpl(new AsciiEncoder(), new ArobaseStringCompressor2(),
			new CompressionZlib());

	public String decode(String code) throws IOException, DataFormatException {
		return zlib.decode(code);
	}

}
