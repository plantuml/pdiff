package com.pdiff.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DbFileAfterRun implements Comparable<DbFileAfterRun> {

	private final Path pumlPath;
	private final String runcode;
	private final JsonObject jsonObject;

	public static Optional<DbFileAfterRun> load(Path pumlPath, String runcode) {
		try {
			final String jsonFileName = pumlPath.getFileName().toString().replace(".puml", ".json");

			Path jsonPath = pumlPath.subpath(1, pumlPath.getNameCount()).resolveSibling(jsonFileName);
			jsonPath = Paths.get("runs", runcode).resolve(jsonPath);

			final String content = new String(Files.readAllBytes(jsonPath));

			return Optional
					.of(new DbFileAfterRun(pumlPath, runcode, JsonParser.parseString(content).getAsJsonObject()));
		} catch (Exception e) {
			return Optional.empty();

		}
	}

	public boolean sameResultAs(DbFileAfterRun other) {
		return this.getCrc() == other.getCrc();
	}

	@Override
	public String toString() {
		return super.toString() + " " + jsonObject.toString();
	}

	private DbFileAfterRun(Path pumlPath, String runcode, JsonObject jsonObject) {
		this.pumlPath = pumlPath;
		this.runcode = runcode;
		this.jsonObject = jsonObject;
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
		return jsonObject;
	}

	@Override
	public int compareTo(DbFileAfterRun other) {
		return this.pumlPath.compareTo(other.pumlPath);
	}

	public Path getPumlPath() {
		return pumlPath;
	}

	public int getCrc() {
		final JsonObject pngObject = jsonObject.getAsJsonObject("png");
		return pngObject.get("crc").getAsInt();
	}

	public int getWidth() {
		final JsonObject pngObject = jsonObject.getAsJsonObject("png");
		return pngObject.get("width").getAsInt();
	}

	public int getDuration() {
		return jsonObject.get("duration").getAsInt();
	}

	public int getHeight() {
		final JsonObject pngObject = jsonObject.getAsJsonObject("png");
		return pngObject.get("height").getAsInt();
	}

	public String getDescription() {
		return jsonObject.get("description").getAsString();
	}

	public String getRuncode() {
		return runcode;
	}

}
