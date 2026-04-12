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

	private final Path rootHum;

	public DbCollection() throws IOException {
		this.rootHum = Path.of("dbhum");
		Files.createDirectories(this.rootHum);
	}

	public void insertSingleFile(String user, DbFileInsert dbFile) throws IOException {
		final String humhash = dbFile.getHumHash().toValue();
		final Path actualPath = getHumPath(humhash);

		if (Files.exists(actualPath) == false)
			dbFile.exportTo(user, actualPath);

	}

	private Path getHumPath(String humhash) throws IOException {
		// Legacy from the past... Remember August 1981 and MS-DOS 1.0 ?
		// That's why we add an underscore here: to avoid colliding with 
		// reserved device names like CON, PRN, or NUL.
		final Path tmp = rootHum.resolve(humhash.substring(0, 1) + "_" + humhash.substring(1, 3));
		Files.createDirectories(tmp);
		return tmp.resolve(humhash + ".puml");
	}

	public String rewriteMe(DbFile file) throws IOException {
		final String sha1 = file.getContentSha1();
		final String humhash = file.getHumHash().toValue();
		final JsonObject jsonFromDb = file.getJsonFromDbClone();
		jsonFromDb.addProperty("sha1", sha1);
		jsonFromDb.addProperty("humhash", humhash);

		final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		final List<String> list2 = List.of(gson.toJson(jsonFromDb));
		final List<String> allLines = Stream.concat(list2.stream(), file.getContent().stream()).collect(Collectors.toList());

		Files.write(getHumPath(humhash), allLines);
		return humhash;

	}

	public int count() throws IOException {
		final AtomicInteger count = new AtomicInteger();
		try (Stream<Path> paths = Files.walk(rootHum)) {
			paths.filter(Files::isRegularFile).forEach(p -> count.incrementAndGet());
		}
		return count.get();
	}

	public Stream<Path> pathStreams() throws IOException {
		return Files.walk(rootHum) //
				.parallel() //
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
