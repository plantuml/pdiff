package com.pdiff.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pdiff.Introspection;
import com.pdiff.OutputRun;

public class DbFileBeforeRun extends DbFile {

	private final String runcode;

	public DbFileBeforeRun(Path pumlPath, String runcode) throws IOException {
		super(pumlPath);
		this.runcode = runcode;
	}

	private Path transformPath(Path path, String extension) {
		final String directory1 = path.getName(1).toString();
		final String newFileName = path.getFileName().toString().replace(".puml", extension);

		return Paths.get("runs", runcode, directory1, newFileName);

	}

	private DbFileAfterRun getDbFileAfterRun() {
		final Optional<DbFileAfterRun> res = DbFileAfterRun.load(getPumlPath(), runcode);
		if (res.isEmpty())
			return null;
		return res.get();
	}

	public long convertMe(int minimalPrefix, boolean onlyPerf) throws Exception {
		List<String> all = Files.readAllLines(getPumlPath());
		all = all.subList(DbCollection.getStartingLine(all), all.size());

		final String text = String.join("\n", all);
		final Path outputPathPng = transformPath(getPumlPath(), ".png");
		final Path outputPathJson = transformPath(getPumlPath(), ".json");
		Files.createDirectories(outputPathPng.getParent());

		final long start = System.currentTimeMillis();
		final OutputRun outputRun = Introspection.outputImage(outputPathPng, text, onlyPerf);

		final long sizeInBytes = Files.size(outputPathPng);

		final long duration = System.currentTimeMillis() - start;

		final BufferedImage bufferedImage = ImageIO.read(outputPathPng.toFile());
		final ImageSignature imageSignature = ImageSignature.fromImage(bufferedImage);

		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("duration", duration);
		jsonObject.addProperty("description", outputRun.description());
		jsonObject.addProperty("implementation", outputRun.clazz());

		final JsonObject png = new JsonObject();
		png.addProperty("width", bufferedImage.getWidth());
		png.addProperty("height", bufferedImage.getHeight());
		png.addProperty("crc", imageSignature.getCrc());
		png.addProperty("sizeInBytes", sizeInBytes);

		jsonObject.add("png", png);

		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Files.write(outputPathJson, List.of(gson.toJson(jsonObject)));
		
		DbFileAfterRun.cache(outputPathJson, jsonObject);
		

		return sizeInBytes;

	}

	private static String getOtherLink(DbFileBeforeRun other) {
		final String name = other.getFileName().replace(".puml", ".html");
		return "../" + name.substring(0, 2) + "/" + name;
	}

	public void createStandaloneHtml(int minimalPrefix, DbFileBeforeRun prev, DbFileBeforeRun next) throws IOException {
		final Path outputPathHtml = transformPath(getPumlPath(), ".html");

		List<String> all = Files.readAllLines(getPumlPath());
		all = all.subList(DbCollection.getStartingLine(all), all.size());

		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(outputPathHtml))) {
			final StringBuilder sb = new StringBuilder();
			println(sb, "<html><head><title>" + getFileName(minimalPrefix) + " - " + this.runcode + "</title>");
			println(sb,
					"""
									    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/brython@3.9.5/brython.min.js"></script>
							<style>
							       .input-field {
							           width: 400px;
							           padding: 1px;
									font-family: "Courier New", Courier, monospace;
							       }
							       .dropdown {
							           display: none;
							           position: absolute;
							           background-color: white;
							           border: 1px solid #ccc;
									min-width: 400px;
							           margin-top: 0px;
							           padding: 0;
							           list-style: none;
							           box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
							       }
							       .dropdown-item {
							           padding: 3px;
							           cursor: pointer;
							           font-family: "Courier New", Courier, monospace;
							       }
							       .dropdown-item:hover {
							           background-color: #eee;
							       }
							</style>
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
			println(sb, "</head>");
			println(sb, "<body onload='brython()'>");

			println(sb, """
					<script type="text/python">
					from browser import document, html, window
					full = dict()
					desc = dict()
										""");
			println(sb, "runcode='" + runcode + "'");

			println(sb, """
					def create_dropdown():
					    dropdown.clear()
					    if input_field.value:
					        dropdown.style.display = 'block'
					        count = 0
					        for name in full.keys():
					            if count<15 and name.startswith(input_field.value):
					                item = html.LI(name + " - " + desc[name], Class='dropdown-item')
					                item.bind('click', on_item_click)
					                dropdown <= item
					                count = count + 1
					    else:
					        dropdown.style.display = 'none'

					def on_item_click(event):
						dropdown.style.display = 'none'
						text_parts = event.target.text.split()
						selected_text = text_parts[0] if text_parts else ''
						long_text = full[selected_text]
						destination_url = f"{runcode}/{long_text[0:2]}/{long_text}.html"
						current_location = window.location.href
						base_url = current_location[:current_location.rfind('/runs/')]
						full_url = f"{base_url}/runs/{destination_url}"
						print(full_url)
						window.location.href = full_url


					def on_input(event):
					    create_dropdown()

					input_field = document['input-field']
					input_field.bind('input', on_input)

					dropdown = document['dropdown']
					result = document['result']
					    </script>					""");

			final String home = "../../../" + runcode + ".html";
			println(sb, "<a href='" + home + "'>Home</a>");

			if (prev != null)
				println(sb, "<a href='" + getOtherLink(prev) + "'>Previous</a>");
			if (next != null)
				println(sb, "<a href='" + getOtherLink(next) + "'>Next</a>");

			println(sb, """
					<hr>
					   <input type="text" id="input-field" class="input-field" placeholder="Type an id here...">
					   <ul id="dropdown" class="dropdown"></ul>
					   <div id="result"></div>
					<hr>
					""");
			println(sb, "<h2>" + getFileName(minimalPrefix) + "</h2>");

			final DbFileAfterRun dbFileAfterRun = getDbFileAfterRun();
			if (dbFileAfterRun != null) {
				final String url = dbFileAfterRun.getUrl();
				if (url != null)
					println(sb, "<a href='" + url + "'>" + url + "</a>");
			}

			println(sb, "<hr>");
			final String src_image = getFileName().replace(".puml", ".png");
			println(sb, "<img src='" + src_image + "'>");
			println(sb, "<hr>");
			println(sb, "<div class='code-container'>");
			print(sb, "<pre><code>");
			for (String s : all)
				println(sb, StringEscapeUtils.escapeHtml4(s));
			println(sb, "</code></pre>");
			println(sb, "</div>");
			println(sb, "</body>");
			println(sb, "</html>");

			pw.print(sb);
		}
	}

	private void println(StringBuilder sb, String data) {
		sb.append(data);
		sb.append("\n");
	}

	private void print(StringBuilder sb, String data) {
		sb.append(data);
	}

}
