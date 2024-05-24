package com.pdiff.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.pdiff.core.DbCollection;
import com.pdiff.core.DbFileAfterRun;

public class HtmlRun {

	private final String runcode;
	private final int minimalPrefix;

	public HtmlRun(Path outHtml, DbCollection dbCollection) throws IOException {

		this.runcode = outHtml.getFileName().toString().replace(".html", "");
		this.minimalPrefix = dbCollection.getMinimalPrefix();

		final List<DbFileAfterRun> all = dbCollection.streamsAfterRun(runcode).sorted().sequential()
				.collect(Collectors.toList());

		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(outHtml))) {
			pw.println("<html><head><title>" + this.runcode + "</title>");
			pw.println(
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
							        table {
							            border-collapse: collapse;
							            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
							            margin-bottom: 20px;
							        }
							        th, td {
							            border: 1px solid #ddd;
							            padding: 8px;
							            text-align: left;
							        }
							        tr:nth-child(even) {
							            background-color: #f9f9f9;
							        }
							        tr:nth-child(odd) {
							            background-color: #fff;
							        }
							        th {
							            background-color: #ddd;
							        }
							        tr:hover {
							            background-color: #f1f1f1;
							        }
							    </style>
							</head>
							<body onload="brython()">
							    <input type="text" id="input-field" class="input-field" placeholder="Type a name here...">
							    <ul id="dropdown" class="dropdown"></ul>
							    <div id="result"></div>

							    <script type="text/python">
							from browser import document, html, window
							full = dict()
							desc = dict()
									 """);

			all.stream().forEach(file -> {
				final String key = file.getFileName(minimalPrefix).replace(".puml", "");
				final String name = file.getFileName().replace(".puml", "");
				final JsonObject json = file.getJsonObject();
				final String description = json.get("description").getAsString();
				pw.println("full['" + key + "']='" + name + "'");
				pw.println("desc['" + key + "']='" + description + "'");
			});

			pw.println("runcode='" + runcode + "'");

			pw.println("""
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
						detail_div = document['detail']
						detail_div.clear()
						text_parts = event.target.text.split()
						selected_text = text_parts[0] if text_parts else ''
						long_text = full[selected_text]
						detail_div <= html.P(f"<b>{selected_text}</b>")
						img_url = f"{runcode}/{long_text[0:2]}/{long_text}.png"
						detail_div <= html.IMG(src=img_url)
						detail_div.style.display = 'block'

					def on_input(event):
					    create_dropdown()

					input_field = document['input-field']
					input_field.bind('input', on_input)

					dropdown = document['dropdown']
					result = document['result']
					    </script>					""");

			pw.println("<p>");
			pw.println("<p>");

			pw.println("<div id=detail></div>");

//			all.stream().forEach(file -> {
//				try {
//					outSingleFile(pw, file);
//				} catch (Exception e) {
//					System.out.println(e);
//				}
//			});

			pw.println("<p>");
			pw.println("<p>");

			pw.println("<table>");

			pw.println("<tr>");
			pw.println("<th>Name</th>");
			pw.println("<th>Duration</th>");
			pw.println("<th>Width</th>");
			pw.println("<th>Height</th>");
			pw.println("<th>Description</th>");
			pw.println("</tr>");

			all.stream().forEach(file -> {
				try {
					final String name = file.getFileName(minimalPrefix).replace(".puml", "");
					pw.println("<tr>");
					pw.println("<td>");
					pw.print(name);
					pw.println("</td>");
					pw.println("<td>");
					pw.print(file.getDuration());
					pw.println("</td>");
					pw.println("<td>");
					pw.print(file.getWidth());
					pw.println("</td>");
					pw.println("<td>");
					pw.print(file.getHeight());
					pw.println("</td>");
					pw.println("<td>");
					pw.print(file.getDescription());
					pw.println("</td>");
					pw.println("</tr>");
				} catch (Exception e) {
					System.out.println(e);
				}
			});

			pw.println("</table>");

			pw.println("""
					</html>
					""");
		}
	}

//	private void outSingleFile(PrintWriter pw, DbFileForRun file) throws IOException {
//		final String name = file.getFileName(minimalPrefix).replace(".puml", "");
//		pw.print("<div class=detail_result id=" + name + ">");
//		pw.println("<hr>");
//		pw.print("<div class=name>");
//		pw.print(name);
//		pw.println("</div>");
//		final JsonObject json = file.getJsonObject();
//		final JsonObject pngObject = json.getAsJsonObject("png");
//		final int width = pngObject.get("width").getAsInt();
//		final int height = pngObject.get("height").getAsInt();
//		pw.println("<img width=" + width + " height=" + height + " src='" + runcode + "/" + file.getJavaScriptPngPath()
//				+ "'>");
//		pw.println("</div>");
//
//	}
}
