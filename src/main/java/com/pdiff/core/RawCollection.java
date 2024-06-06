package com.pdiff.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.fusesource.jansi.Ansi;

public class RawCollection {

	private final Path root;

	public RawCollection() {
		this.root = Path.of("raw");
	}

	public void iterateMeAndInsert(String user, DbCollection dbCollection) throws IOException {
		System.out.println();
		System.out.println();
		final int before = dbCollection.count();
		try (Stream<Path> paths = Files.walk(root)) {
			paths.filter(Files::isRegularFile).forEach(p -> {
				try {
					final RawFile rawFile = new RawFile(p);
					rawFile.process(user, dbCollection);
				} catch (IOException e) {
					System.out.print(Ansi.ansi().cursorUpLine());
					System.out.print(Ansi.ansi().eraseLine());
					System.out.print(Ansi.ansi().cursorUpLine());
					System.out.print(Ansi.ansi().eraseLine());
					System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Error " + p).reset());
					System.out.println();
					System.out.println();
				}
			});
		}
		final int after = dbCollection.count();
		System.out.print(Ansi.ansi().cursorUpLine());
		System.out.print(Ansi.ansi().eraseLine());
		System.out.print(Ansi.ansi().cursorUpLine());
		System.out.print(Ansi.ansi().eraseLine());
		System.out.println("Collection size: " + before + " -> " + after);

	}

}
