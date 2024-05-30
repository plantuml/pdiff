package com.pdiff.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
							    <input type="text" id="input-field" class="input-field" placeholder="Type an id here...">

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
					def on_input(event):
					    if input_field.value:
						    for tr in document.select('tr'):
								if tr.id.startswith(input_field.value) or tr.id=='0h':
									tr.style.display = ''
								else:
									tr.style.display = 'none'
					    else:
							for tr in document.select('tr'):
								tr.style.display = ''


					input_field = document['input-field']
					input_field.bind('input', on_input)

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

			pw.println("<tr id=0h>");
			pw.println("<th>Name</th>");
			pw.println("<th>Duration</th>");
			pw.println("<th>Width</th>");
			pw.println("<th>Height</th>");
			pw.println("<th>Description</th>");
			pw.println("<th>URL</th>");
			pw.println("</tr>");

			all.stream().forEach(file -> {
				try {
					final String name = file.getFileName(minimalPrefix).replace(".puml", "");
					pw.println("<tr id=" + name + ">");
					pw.println("<td>");
					final String link = runcode + "/" + file.getFileName().substring(0, 2) + "/"
							+ file.getFileName().replace(".puml", ".html");
					pw.print("<a href='runs/" + link + "'>" + name + "</a>");
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
					pw.println("<td>");
					final String url = file.getUrl();
					if (url != null) {
						pw.print("<a href='" + url + "'>" + url + "</a>");
					}
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

}
