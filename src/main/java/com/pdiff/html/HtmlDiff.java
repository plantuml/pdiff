package com.pdiff.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import com.pdiff.core.Cmp;
import com.pdiff.core.DbFileAfterRun;

public class HtmlDiff {

	public HtmlDiff(Path outHtml, int minimalPrefix, Collection<Cmp> cmps) throws IOException {

		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(outHtml))) {
			pw.println("<html><head><title>" + outHtml.getFileName() + "</title>");
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
							        table img {
							            max-width: 95%;
							            height: auto;
							        }
							        th, td {
							            border: 1px solid #ddd;
							            padding: 8px;
							            text-align: left;
							        }
							        tr:nth-child(3n) {
							            background-color: #e9e9f9;
							        }
							        tr:nth-child(3n+1) {
							            background-color: #fff;
							        }
							        tr:nth-child(3n+2) {
							            background-color: #e9e9f9;
							        }
							        th {
							            background-color: #ddd;
							        }
							        tr:hover {
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

//			all.stream().forEach(file -> {
//				final String key = file.getFileName(minimalPrefix).replace(".puml", "");
//				final String name = file.getFileName().replace(".puml", "");
//				final JsonObject json = file.getJsonObject2();
//				final String description = json.get("description").getAsString();
//				pw.println("full['" + key + "']='" + name + "'");
//				pw.println("desc['" + key + "']='" + description + "'");
//			});
//
//			pw.println("runcode='" + runcode + "'");

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

			pw.println("<p>");
			pw.println("<p>");

			String run1name = null;
			String run2name = null;

			for (Cmp cmp : cmps) {
				if (cmp.getRun1() != null && cmp.getRun2() != null) {
					run1name = cmp.getRun1().getRuncode();
					run2name = cmp.getRun2().getRuncode();
					break;
				}
			}

			pw.println("<table>");
			pw.println("<tr>");
//			pw.println("<th>name</th>");
			pw.println("<th colspan=2>" + run1name + "</th>");
			pw.println("<th colspan=2>" + run2name + "</th>");
			pw.println("</tr>");

			for (Cmp cmp : cmps) {
				if (cmp.bothPresent() == false)
					continue;
				if (cmp.isSame())
					continue;

				final DbFileAfterRun run1 = cmp.getRun1();
				final DbFileAfterRun run2 = cmp.getRun2();
				pw.println("<tr>");
				pw.println("<td colspan=4>");
				pw.print("<center><b>" + run1.getFileName(minimalPrefix) + "</b></center>");
				pw.println("</td>");
				pw.println("</tr>");
				pw.println("<tr>");

//				if (run1.sameDescriptionAndImplementation(run2)) {
//					pw.println("<td colspan=2>");
//					pw.print(run1.getDescription());
//					pw.println("</td>");
//					pw.println("<td colspan=2>");
//					pw.print(run1.getImplementation());
//					pw.println("</td>");
//				} else {
				pw.println("<td>");
				pw.print(run1.getDescription());
				pw.println("</td>");
				pw.println("<td>");
				pw.print(run1.getImplementation());
				pw.println("</td>");
				pw.println("<td>");
				pw.print(run2.getDescription());
				pw.println("</td>");
				pw.println("<td>");
				pw.print(run2.getImplementation());
				pw.println("</td>");
//				}
				pw.println("</tr>");

				pw.println("<tr>");
//				pw.println("<td>");
//				pw.print("1");
//				pw.println("</td>");
				pw.println("<td colspan=2>");
				pw.println("<img src=runs/" + run1.getRuncode() + "/" + run1.getJavaScriptPngPath() + ">");
				pw.println("</td>");
				pw.println("<td colspan=2>");
				pw.println("<img src=runs/" + run2.getRuncode() + "/" + run2.getJavaScriptPngPath() + ">");
				pw.println("</td>");
//				pw.println("<td colspan=3>");
//				pw.println("<img src=runs/" + run1.getRuncode() + "/" + run1.getJavaScriptPngPath() + ">");
//				pw.println("<hr>");
//				pw.println("<img src=runs/" + run2.getRuncode() + "/" + run2.getJavaScriptPngPath() + ">");
//				pw.println("</td>");
				pw.println("</tr>");

			}

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
