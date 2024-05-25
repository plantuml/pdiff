package com.pdiff.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DbFileAfterRun implements Comparable<DbFileAfterRun> {

	private final Path pumlPath;
	private final String runcode;
	private final JsonObject jsonFromRun;
	private final JsonObject jsonFromDb;

	public static Optional<DbFileAfterRun> load(Path pumlPath, String runcode) {
		try {

			List<String> all = Files.readAllLines(pumlPath);
			all = all.subList(0, DbCollection.getStartingLine(all));
			final String text = String.join("\n", all);

			final JsonObject jsonFromDb = JsonParser.parseString(text).getAsJsonObject();

			final String jsonFileName = pumlPath.getFileName().toString().replace(".puml", ".json");

			Path jsonPath = pumlPath.subpath(1, pumlPath.getNameCount()).resolveSibling(jsonFileName);
			jsonPath = Paths.get("runs", runcode).resolve(jsonPath);

			final String content = new String(Files.readAllBytes(jsonPath));

			final JsonObject jsonFromRun = JsonParser.parseString(content).getAsJsonObject();
			return Optional.of(new DbFileAfterRun(pumlPath, runcode, jsonFromRun, jsonFromDb));
		} catch (Exception e) {
			return Optional.empty();

		}
	}

	public static void main(String[] args) {
		Path p = Paths.get("db", "1i", "1ihwmyyohxz86m9ttam1frigvrjw6e0.puml");
		System.out.println("p=" + p);
		load(p, "toto");
	}

	public boolean sameResultAs(DbFileAfterRun other) {
		return this.getCrc() == other.getCrc();
	}

	@Override
	public String toString() {
		return super.toString() + " " + jsonFromRun.toString();
	}

	private DbFileAfterRun(Path pumlPath, String runcode, JsonObject jsonFromRun, JsonObject jsonFromDb) {
		this.pumlPath = pumlPath;
		this.runcode = runcode;
		this.jsonFromRun = jsonFromRun;
		this.jsonFromDb = jsonFromDb;
	}

	public String getFileName(int minimalPrefix) {
		return getFileName().substring(0, minimalPrefix);
	}

	public String getFileName() {
		return pumlPath.getFileName().toString();
	}

	public String getJavaScriptPngPath() {
		final Path newPath = pumlPath.subpath(1, pumlPath.getNameCount());
		return newPath.toString().replace(".puml", ".png").replace('\\', '/');
	}

	public JsonObject getJsonObject() {
		return jsonFromRun;
	}

	@Override
	public int compareTo(DbFileAfterRun other) {
		return this.pumlPath.compareTo(other.pumlPath);
	}

	public Path getPumlPath() {
		return pumlPath;
	}

	public int getCrc() {
		final JsonObject pngObject = jsonFromRun.getAsJsonObject("png");
		return pngObject.get("crc").getAsInt();
	}

	public int getWidth() {
		final JsonObject pngObject = jsonFromRun.getAsJsonObject("png");
		return pngObject.get("width").getAsInt();
	}

	public int getDuration() {
		return jsonFromRun.get("duration").getAsInt();
	}

	public int getHeight() {
		final JsonObject pngObject = jsonFromRun.getAsJsonObject("png");
		return pngObject.get("height").getAsInt();
	}

	public String getUrl() {
		final JsonObject insertion = jsonFromDb.getAsJsonObject("insertion");
		if (insertion.has("url"))
			return insertion.get("url").getAsString();
		return null;
	}

	public String getDescription() {
		return jsonFromRun.get("description").getAsString();
	}

	public String getRuncode() {
		return runcode;
	}

}
