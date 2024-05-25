package com.pdiff.core;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
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

	private Path transformPath(Path path, String extension) {
		final String directory1 = path.getName(1).toString();
		final String newFileName = path.getFileName().toString().replace(".puml", extension);

		return Paths.get("runs", runcode, directory1, newFileName);

	}

	public void convertMe(int minimalPrefix) throws Exception {
		List<String> all = Files.readAllLines(pumlPath);
		all = all.subList(DbCollection.getStartingLine(all), all.size());

		final String text = String.join("\n", all);
		final Path outputPathPng = transformPath(pumlPath, ".png");
		final Path outputPathJson = transformPath(pumlPath, ".json");
		final Path outputPathHtml = transformPath(pumlPath, ".html");
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

		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(outputPathHtml))) {
			pw.println("<html><head><title>" + getFileName(minimalPrefix) + " - " + this.runcode + "</title>");
			pw.println("""
					<style>
					    h2 {
					        text-align: center;
					        color: #333;
					    }
					    hr {
					        border: 0;
					        height: 1px;
					        background: #ddd;
					        margin: 20px 0;
					    }
					    .code-container {
					        background-color: #f9f9f9;
					        border: 1px solid #ddd;
					        border-radius: 8px;
					        padding: 0px 10px;
					    }
					</style>
						""");
			pw.println("</head>");
			pw.println("<body>");
			pw.println("<h2>" + getFileName(minimalPrefix) + "</h2>");
			pw.println("<hr>");
			final String src_image = getFileName().replace(".puml", ".png");
			pw.println("<img src='" + src_image + "'>");
			pw.println("<hr>");
			pw.println("<div class='code-container'>");
			pw.print("<pre><code>");
			for (String s : all)
				pw.println(s);
			pw.println("</code></pre>");
			pw.println("</div>");
			pw.println("</body>");
			pw.println("</html>");

		}

	}

}
