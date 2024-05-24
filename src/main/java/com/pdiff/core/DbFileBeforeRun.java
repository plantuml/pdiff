package com.pdiff.core;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pdiff.Introspection;

public class DbFileBeforeRun {

	private final Path pumlPath;
	private final String runcode;

	public DbFileBeforeRun(Path pumlPath, String runcode) {
		this.pumlPath = pumlPath;
		this.runcode = runcode;
	}

	public String getFileName(int minimalPrefix) {
		return getFileName().substring(0, minimalPrefix);
	}

	public String getFileName() {
		return pumlPath.getFileName().toString();
	}

	private static int getStartingLine(List<String> lines) {
		for (int i = 0; i < lines.size(); i++)
			if (lines.get(i).startsWith("@start"))
				return i;

		throw new IllegalStateException();

	}

	private Path transformPath(Path path, String extension) {
		final String directory1 = path.getName(1).toString();
		final String newFileName = path.getFileName().toString().replace(".puml", extension);

		return Paths.get("runs", runcode, directory1, newFileName);

	}

	public void convertMe() throws Exception {
		List<String> all = Files.readAllLines(pumlPath);
		all = all.subList(getStartingLine(all), all.size());

		final String text = String.join("\n", all);
		final Path outputPathPng = transformPath(pumlPath, ".png");
		final Path outputPathJson = transformPath(pumlPath, ".json");
		Files.createDirectories(outputPathPng.getParent());

		final long start = System.currentTimeMillis();
		final String description = Introspection.outputImage(outputPathPng, text);

		final long duration = System.currentTimeMillis() - start;

		final BufferedImage bufferedImage = ImageIO.read(outputPathPng.toFile());
		final ImageSignature imageSignature = ImageSignature.fromImage(bufferedImage);

		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("duration", duration);
		jsonObject.addProperty("description", description);

		final JsonObject png = new JsonObject();
		png.addProperty("width", bufferedImage.getWidth());
		png.addProperty("height", bufferedImage.getHeight());
		png.addProperty("crc", imageSignature.getCrc());

		jsonObject.add("png", png);

		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Files.write(outputPathJson, List.of(gson.toJson(jsonObject)));

	}

}
