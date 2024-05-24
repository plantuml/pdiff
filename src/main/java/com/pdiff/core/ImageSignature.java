package com.pdiff.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

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
		final CRC32 crc = new CRC32();
		for (int x = 0; x < im.getWidth(); x++) {
			for (int y = 0; y < im.getHeight(); y++) {
				if (ignoreFirstPoint) {
					ignoreFirstPoint = false;
				} else {
					final int c = im.getRGB(x, y);
					updateCRC(crc, c);
				}
			}
		}
		this.width = im.getWidth();
		this.height = im.getHeight();
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

	private void updateCRC(CRC32 crc, int c) {
		final int r = (c & 0xFF0000) >> 16;
		final int g = (c & 0x00FF00) >> 8;
		final int b = (c & 0x0000FF);
		crc.update(r);
		crc.update(g);
		crc.update(b);
	}

	public final long getCrc() {
		return crc;
	}

}
