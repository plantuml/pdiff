package com.pdiff.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class DbCollection {

	private final Path root;

	public DbCollection() throws IOException {
		this.root = Path.of("db");
		Files.createDirectories(this.root);
	}

	private Path getActualPath(String sha1) throws IOException {
		final Path tmp = root.resolve(sha1.substring(0, 2));
		Files.createDirectories(tmp);
		final Path out = tmp.resolve(sha1 + ".puml");
		return out;

	}

	public void insertSingleFile(String user, DbFileInsert dbFile) throws IOException {
		final String sha1 = dbFile.getSha1();
		final Path actualPath = getActualPath(sha1);

		if (Files.exists(actualPath) == false)
			dbFile.exportTo(user, actualPath);

	}

	public String rewriteMe(DbFile file) throws IOException {
		final String sha1 = file.getContentSha1();
		final Path actualPath = getActualPath(sha1);
		final JsonObject jsonFromDb = file.getJsonFromDbClone();
		jsonFromDb.addProperty("sha1", sha1);

		final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		final List<String> list2 = List.of(gson.toJson(jsonFromDb));
		final Stream<String> combinedStream = Stream.concat(list2.stream(), file.getContent().stream());

		Files.write(actualPath, combinedStream.collect(Collectors.toList()));
		return sha1;

	}

	public int count() throws IOException {
		final AtomicInteger count = new AtomicInteger();
		try (Stream<Path> paths = Files.walk(root)) {
			paths.filter(Files::isRegularFile).forEach(p -> count.incrementAndGet());
		}
		return count.get();
	}

	public Stream<Path> pathStreams() throws IOException {
		return Files.walk(root) //
				.filter(Files::isRegularFile) //
				.filter(p -> p.toString().endsWith(".puml")) //
				.parallel();
	}

	public Stream<DbFileAfterRun> streamsAfterRun(String runcode) throws IOException {
		return pathStreams().map(p -> DbFileAfterRun.load(p, runcode)) //
				.filter(Optional::isPresent) //
				.map(Optional::get);
	}

	public Stream<DbFileBeforeRun> streamsBeforeRun(String runcode) throws IOException {
		return pathStreams().map(p -> {
			try {
				return new DbFileBeforeRun(p, runcode);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public Stream<DbFile> streamsDbFile() throws IOException {
		return pathStreams().map(p -> {
			try {
				return new DbFile(p);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static int getMinimalPrefixLengthForThose(String s1, String s2) {
		if (s1.equals(s2))
			throw new IllegalArgumentException();

		int result = 0;
		while (s1.charAt(result) == s2.charAt(result))
			result++;
		return result + 1;
	}

	public int getMinimalPrefix() throws IOException {
		final List<String> fileNames = pathStreams() //
				.map(path -> path.getFileName().toString()) //
				.sorted() //
				.collect(Collectors.toList());

		if (fileNames.size() < 2)
			throw new IllegalStateException("Not enough files to determine minimal prefix");

		int result = 0;
		for (int i = 0; i < fileNames.size() - 1; i++) {
			final int tmp = getMinimalPrefixLengthForThose(fileNames.get(i), fileNames.get(i + 1));
			if (tmp > result)
				result = tmp;

		}
		return result;
	}

	public static void main(String[] args) {
		System.err.println(getMinimalPrefixLengthForThose("ABCD", "ABC1"));
	}

	public static int getStartingLine(List<String> lines) {
		for (int i = 0; i < lines.size(); i++)
			if (lines.get(i).startsWith("@start"))
				return i;

		throw new IllegalStateException();

	}

}
