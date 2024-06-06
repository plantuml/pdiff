package com.pdiff.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DbFileAfterRun extends DbFile {

	private final String runcode;
	private final JsonObject jsonFromRun;

	public static Optional<DbFileAfterRun> load(Path pumlPath, String runcode) {
		try {

			final String jsonFileName = pumlPath.getFileName().toString().replace(".puml", ".json");

			Path jsonPath = pumlPath.subpath(1, pumlPath.getNameCount()).resolveSibling(jsonFileName);
			jsonPath = Paths.get("runs", runcode).resolve(jsonPath);

			final String content = new String(Files.readAllBytes(jsonPath));

			final JsonObject jsonFromRun = JsonParser.parseString(content).getAsJsonObject();
			return Optional.of(new DbFileAfterRun(pumlPath, runcode, jsonFromRun));
		} catch (Exception e) {
			return Optional.empty();

		}
	}

	public boolean sameResultAs(DbFileAfterRun other) {
		return other != null && this.getCrc() == other.getCrc();
	}

	@Override
	public String toString() {
		return super.toString() + " " + jsonFromRun.toString();
	}

	private DbFileAfterRun(Path pumlPath, String runcode, JsonObject jsonFromRun) throws IOException {
		super(pumlPath);
		this.runcode = runcode;
		this.jsonFromRun = jsonFromRun;
	}

	public JsonObject getJsonObject() {
		return jsonFromRun;
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

	public String getDescription() {
		return jsonFromRun.get("description").getAsString();
	}

	public String getImplementation() {
		return jsonFromRun.get("implementation").getAsString();
	}

	public String getRuncode() {
		return runcode;
	}

	public boolean sameDescriptionAndImplementation(DbFileAfterRun other) {
		return this.getDescription().equals(other.getDescription())
				&& this.getImplementation().equals(other.getImplementation());
	}

}
