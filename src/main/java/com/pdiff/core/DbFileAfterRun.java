package com.pdiff.core;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class DbFileAfterRun extends DbFile {

	private final String runcode;
	private final JsonObject jsonFromRun;

	private final static Map<Path, JsonObject> cache = new ConcurrentHashMap<>();

	public static void cache(Path outputPathJson, JsonObject jsonObject) {
		cache.put(outputPathJson.toAbsolutePath(), jsonObject);
	}

	public static Optional<DbFileAfterRun> load(Path pumlPath, String runcode) {
		try {

			final String jsonFileName = pumlPath.getFileName().toString().replace(".puml", ".json");

			Path jsonPath = pumlPath.subpath(1, pumlPath.getNameCount()).resolveSibling(jsonFileName);
			jsonPath = Paths.get("runs", runcode).resolve(jsonPath);

			JsonObject jsonFromRun = cache.get(jsonPath.toAbsolutePath());
			if (jsonFromRun != null)
				return Optional.of(new DbFileAfterRun(pumlPath, runcode, jsonFromRun));

			try (Reader reader = Files.newBufferedReader(jsonPath, StandardCharsets.UTF_8)) {
				final JsonReader jsonReader = new JsonReader(reader);
				jsonFromRun = JsonParser.parseReader(jsonReader).getAsJsonObject();
				return Optional.of(new DbFileAfterRun(pumlPath, runcode, jsonFromRun));
			}
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
