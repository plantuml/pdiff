package com.pdiff.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import org.fusesource.jansi.Ansi;

import com.pdiff.code.TranscoderSmart;

public class RawFile {

	private static final Pattern patternUrl = Pattern.compile("(https?://[^\\s'\"]+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern patternSrc = Pattern.compile("SRC=\\[([-_0-9a-zA-Z]+)\\]");

	private final Path path;
	private final List<String> content;
	// private final String sha1;

	public RawFile(Path path) throws IOException {
		this.path = path;
		this.content = Files.readAllLines(path);
		// this.sha1 = SHA1.calculateSHA1(content);
		uncommentLines();
	}

	private static String detectUrl(String s) {
		final Matcher matcher = patternUrl.matcher(s);
		if (matcher.find())
			return matcher.group();

		return null;
	}

	private static String detectSrc(String s) {
		final Matcher matcher = patternSrc.matcher(s);
		if (matcher.find())
			return matcher.group(1);

		return null;
	}

	public void process(String user, DbCollection dbCollection) throws IOException {
		System.out.print(Ansi.ansi().cursorUpLine());
		System.out.print(Ansi.ansi().eraseLine());
		System.out.print(Ansi.ansi().cursorUpLine());
		System.out.print(Ansi.ansi().eraseLine());
		System.out.println("Reading " + path);
		System.out.println();

		String url = null;

		for (final Iterator<String> it = content.iterator(); it.hasNext();) {
			String s = it.next();

			final String src = detectSrc(s);
			if (src != null) {
				try {
					final String raw = new TranscoderSmart().decode(src);
					final DbFileInsert dbFile = new DbFileInsert();
					dbFile.setUrl(url);
					url = null;
					dbFile.append(raw);
					final String sha1 = dbFile.getSha1();
					System.out.print(Ansi.ansi().cursorUpLine());
					System.out.print(Ansi.ansi().eraseLine());
					System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("(" + sha1 + ")").reset());
					dbCollection.insertSingleFile(user, dbFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			final String tmp = detectUrl(s);
			if (tmp != null)
				url = tmp;

			if (s.startsWith("@start")) {
				final DbFileInsert dbFile = new DbFileInsert();
				dbFile.setUrl(url);
				url = null;
				dbFile.append(s);
				do {
					s = it.next();
					dbFile.append(s);
					if (s.startsWith("@end")) {
						final String sha1 = dbFile.getSha1();
						System.out.print(Ansi.ansi().cursorUpLine());
						System.out.print(Ansi.ansi().eraseLine());
						System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("(" + sha1 + ")").reset());
						dbCollection.insertSingleFile(user, dbFile);
						break;
					}
				} while (it.hasNext());
			}
		}

		// System.out.println();

	}

	private void uncommentLines() {
		String currentPrefix = null;
		for (int i = 0; i < content.size(); i++) {
			String prefix = getStartPrefix(content.get(i));
			if (prefix != null)
				currentPrefix = prefix;
			if (currentPrefix != null && content.get(i).startsWith(currentPrefix))
				content.set(i, content.get(i).substring(currentPrefix.length()));
			if (content.get(i).contains("@end"))
				currentPrefix = null;

		}

	}

	private String getStartPrefix(String s) {
		final int idx = s.indexOf("@start");
		if (idx == -1)
			return null;

		return s.substring(0, idx);
	}

}
