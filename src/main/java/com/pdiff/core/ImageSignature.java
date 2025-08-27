package com.pdiff.core;

import java.awt.image.BufferedImage;
import java.util.zip.CRC32;

public class ImageSignature {

	private final int width;
	private final int height;
	private final long crc;

	private ImageSignature(int size) {
		this.width = size;
		this.height = size;
		this.crc = size;
	}

	private ImageSignature(int width, int height, long crc) {
		this.width = width;
		this.height = height;
		this.crc = crc;
	}

	private ImageSignature(BufferedImage im, boolean ignoreFirstPoint) {
		final int w = im.getWidth();
		final int h = im.getHeight();

		final CRC32 crc = new CRC32();

		final int[] line = new int[w];
		final byte[] buf = new byte[w * 3];

		for (int y = 0; y < h; y++) {
			im.getRGB(0, y, w, 1, line, 0, w);

			int startX = 0;
			if (ignoreFirstPoint) {
				startX = 1;
				ignoreFirstPoint = false;
			}

			int bi = 0;
			for (int x = startX; x < w; x++) {
				final int c = line[x];
				buf[bi++] = (byte) (c >>> 16); // R
				buf[bi++] = (byte) (c >>> 8); // G
				buf[bi++] = (byte) c; // B
			}

			crc.update(buf, 0, bi);

		}

		this.width = w;
		this.height = h;
		this.crc = crc.getValue();
	}


	public static ImageSignature fromImage(BufferedImage im) {
		return new ImageSignature(im, false);
	}

	@Override
	public String toString() {
		if (width == height && width == crc) {
			return "" + width;
		}
		return "" + width + "x" + height + "(" + crc + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object arg) {
		if (arg == null) {
			return false;
		}
		final ImageSignature other = (ImageSignature) arg;
		return this.width == other.width && this.height == other.height && this.crc == other.crc;
	}

//	public boolean matchPng(ImageSignature actual) {
//		return equals(actual);
//	}
//
//	public boolean matchPng(BufferedImage im, boolean ignoreFirstPoint) {
//		return matchPng(fromImage(im, ignoreFirstPoint));
//	}
//
//	public boolean matchPng(File f, boolean ignoreFirstPoint) throws IOException {
//		return matchPng(fromFilePng(f, ignoreFirstPoint));
//	}
//
//	public static ImageSignature fromFilePng(File f, boolean ignoreFirstPoint) throws IOException {
//		final BufferedImage im = ImageIO.read(f);
//		return fromImage(im, ignoreFirstPoint);
//	}

	public final long getCrc() {
		return crc;
	}

}
