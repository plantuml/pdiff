package com.pdiff.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DbFile implements Comparable<DbFile> {

	private final Path pumlPath;
	private final JsonObject jsonFromDb;

	public DbFile(Path pumlPath) throws IOException {
		this.pumlPath = pumlPath;
		List<String> all = Files.readAllLines(pumlPath);
		all = all.subList(0, DbCollection.getStartingLine(all));
		final String text = String.join("\n", all);

		this.jsonFromDb = JsonParser.parseString(text).getAsJsonObject();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + pumlPath.getFileName();
	}

	final public String getFileName(int minimalPrefix) {
		return getFileName().substring(0, minimalPrefix);
	}

	final public String getFileName() {
		return pumlPath.getFileName().toString();
	}

	final public String getJavaScriptPngPath() {
		final Path newPath = pumlPath.subpath(1, pumlPath.getNameCount());
		return newPath.toString().replace(".puml", ".png").replace('\\', '/');
	}

	@Override
	final public int compareTo(DbFile other) {
		return this.pumlPath.compareTo(other.pumlPath);
	}

	final public Path getPumlPath() {
		return pumlPath;
	}

	final public String getUrl() {
		final JsonObject insertion = jsonFromDb.getAsJsonObject("insertion");
		if (insertion.has("url"))
			return insertion.get("url").getAsString();
		return null;
	}

	final public String getJsonSha1() {
		return jsonFromDb.get("sha1").getAsString();
	}

	final public boolean isSha1ok() throws IOException {
		return getContentSha1().equals(getJsonSha1());
	}

	final public String getContentSha1() throws IOException {
		return SHA1.calculateSHA1(getContent());
	}

	final public List<String> getContent() throws IOException {
		List<String> all = Files.readAllLines(pumlPath);
		return all.subList(DbCollection.getStartingLine(all), all.size());
	}

	public JsonObject getJsonFromDb() {
		return jsonFromDb;
	}

	public JsonObject getJsonFromDbClone() throws IOException {
		List<String> all = Files.readAllLines(pumlPath);
		all = all.subList(0, DbCollection.getStartingLine(all));
		final String text = String.join("\n", all);

		return JsonParser.parseString(text).getAsJsonObject();
	}

	public void deleteMe() throws IOException {
		Files.delete(pumlPath);
	}

}
