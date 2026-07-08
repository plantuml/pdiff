package com.pdiff;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.pdiff.core.DbCollection;
import com.pdiff.core.DbFileBeforeRun;

@Parameters(commandDescription = "Export test to Vega format.")
public class VegaCommand {

	@Parameter(names = { "-r", "--run" }, description = "Specifies the name of the run", required = false)
	private String version;

	public void doit() throws Exception {
		final DbCollection dbCollection = new DbCollection();

		if (version == null)
			version = Introspection.versionString();

		final List<DbFileBeforeRun> all = dbCollection.streamsBeforeRun(version) //
				.collect(Collectors.toList());

		final Set<String> types = new TreeSet<>();

		for (DbFileBeforeRun p : all) {
			final String type = processFile(p);
			if (type != null)
				types.add(type);
		}

		System.out.println("types=" + types);

	}

	private String processFile(DbFileBeforeRun f) throws Exception {

		final String type = f.getType();
		switch (type) {
		case "GanttDiagram":
		case "PSystemRegex":
		case "SequenceDiagram":
		case "NwDiagram":
		case "PSystemEbnf":
		case "MindMapDiagram":
		case "PSystemErrorPreprocessor":
		case "TimingDiagram":
		case "WBSDiagram":
		case "WireDiagram":
		case "ActivityDiagram3":
		case "BoardDiagram":
		case "BpmDiagram":
		case "ChartDiagram":
		case "FilesDiagram":
		case "FlowDiagram":
		case "GitDiagram":
		case "Help":
		case "ListSpriteDiagram":
		case "NewpagedDiagram":
		case "PSystemCharlie":
		case "PSystemColors":
		case "PSystemCreole":
		case "PSystemDedication":
		case "PSystemDefinition":
		case "PSystemDitaa":
		case "PSystemDot":
		case "PSystemErrorV2":
		case "PSystemLatex":
		case "PSystemListArchimateSprites":
		case "PSystemListEmoji":
		case "PSystemListFonts":
		case "PSystemListOpenIconic":
		case "PSystemMath":
		case "PSystemOpenIconic":
		case "PSystemSalt":
		case "PSystemSudoku":
		case "PSystemUnsupported":
		case "PSystemVersion":
		case "PSystemWelcome":
			return null;
		}

		final String name = f.getFileName();
		final String sub = f.getSubDirName();
		final List<String> lines = new ArrayList<>(f.getAllLines());
		lines.add(0, "---");
		lines.add(0, "---");
		lines.add(1, "output: svg");

		System.out.println(" sub=" + sub + " name= " + name + " " + type);

		final Path dirPath = Paths.get("vega/smetana", sub);
		Files.createDirectories(dirPath);

		final Path filePath = dirPath.resolve(name);
		Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		return type;

	}
}
