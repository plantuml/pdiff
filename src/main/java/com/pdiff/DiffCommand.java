package com.pdiff;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.pdiff.core.Cmp;
import com.pdiff.core.DbCollection;
import com.pdiff.html.HtmlDiff;

@Parameters(commandDescription = "Compare two different runs")
public class DiffCommand {

	@Parameter(description = "run1 run2")
	private java.util.List<String> parameters;

	public void doit() throws IOException {
		if (parameters == null || parameters.size() != 2) {
			System.err.println("Error: Two arguments are required: <run1> <run2>");
			return;
		}

		final String run1 = parameters.get(0);
		final String run2 = parameters.get(1);

		final DbCollection dbCollection = new DbCollection();

		if (dbCollection.streamsAfterRun(run1).count() == 0) {
			System.out.println("No such run '" + run1 + " in runs/");
			return;
		}

		if (dbCollection.streamsAfterRun(run2).count() == 0) {
			System.out.println("No such run '" + run2 + " in runs/");
			return;
		}

		final SortedMap<Path, Cmp> all = new TreeMap<>();
		dbCollection.streamsAfterRun(run1).forEach(f -> {
			Cmp cmp = new Cmp();
			all.put(f.getPumlPath(), cmp);
			cmp.setRun1(f);
		});
		dbCollection.streamsAfterRun(run2).forEach(f -> {
			Cmp cmp = all.get(f.getPumlPath());
			if (cmp == null)
				cmp = new Cmp();
			cmp.setRun2(f);
		});

		System.out.println("Diff command executed " + all.size());
		System.out.println("Reference result: " + run1);
		System.out.println("Compared result: " + run2);

		for (Cmp cmp : all.values())
			if (cmp.bothPresent() && cmp.isSame() == false)
				System.out.println(cmp);

		final Path outHtml = Paths.get(run1 + "-" + run2 + ".html");

		new HtmlDiff(outHtml, dbCollection.getMinimalPrefix(), all.values());

	}
}
