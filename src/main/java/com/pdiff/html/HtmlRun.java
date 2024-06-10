package com.pdiff.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.fusesource.jansi.Ansi;

import com.pdiff.core.DbFileAfterRun;
import com.pdiff.core.DbFileBeforeRun;

public class HtmlRun {

	public HtmlRun(Path outHtml, List<DbFileBeforeRun> runOk, int minimalPrefix) throws IOException {
		final String runcode = outHtml.getFileName().toString().replace(".html", "");

		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(outHtml))) {
			pw.println("<html><head><title>" + runcode + "</title>");
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
									 """);

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

			pw.println("<p>");
			pw.println("<p>");

			pw.println("<table>");

			pw.println("<tr id=0h>");
			pw.println("<th>Name</th>");
			pw.println("<th>Duration</th>");
			pw.println("<th>Width</th>");
			pw.println("<th>Height</th>");
			pw.println("<th>Implementation</th>");
			pw.println("<th>Description</th>");
			pw.println("<th>URL</th>");
			pw.println("</tr>");

			for (int i = 0; i < runOk.size(); i++) {
				System.out.println("Writing main HTML file " + i + "/" + runOk.size());
				System.out.print(Ansi.ansi().cursorUpLine());
				final Optional<DbFileAfterRun> tmp = DbFileAfterRun.load(runOk.get(i).getPumlPath(), runcode);
				if (tmp.isPresent()) {
					final DbFileAfterRun file = tmp.get();
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
					pw.print(file.getImplementation());
					pw.println("</td>");
					pw.println("<td>");
					pw.print(file.getDescription());
					pw.println("</td>");
					pw.println("<td>");
					final String url = file.getUrl();
					if (url != null)
						pw.print("<a href='" + url + "'>" + url + "</a>");

					pw.println("</td>");
					pw.println("</tr>");
				}
			}

			System.out.print(Ansi.ansi().eraseLine());

			pw.println("</table>");

			pw.println("""
					</html>
					""");
		}
	}

}
