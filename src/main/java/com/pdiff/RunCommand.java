package com.pdiff;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.pdiff.core.DbCollection;
import com.pdiff.core.DbFileAfterRun;
import com.pdiff.core.DbFileBeforeRun;
import com.pdiff.core.MagicOutput;
import com.pdiff.core.NumberedThreadFactory;
import com.pdiff.html.HtmlRun;

@Parameters(commandDescription = "This command runs a PlantUML version on the entire collection")
public class RunCommand {

	@Parameter(names = { "-r", "--run" }, description = "Specifies the name of the run", required = false)
	private String version;

	@Parameter(names = { "-s", "--slot" }, description = "Specifies the number of parallel slots", required = false)
	private int slot = Runtime.getRuntime().availableProcessors();

	private int count = 0;
	private AtomicInteger done = new AtomicInteger();

	private int minimalPrefix;
	private long start;

	private MagicOutput magicOutput;

	public void doit() throws IOException, InterruptedException {

		final DbCollection dbCollection = new DbCollection();
		this.count = dbCollection.count();
		this.minimalPrefix = dbCollection.getMinimalPrefix();

		if (version == null)
			version = Introspection.versionString();

		final ExecutorService executorService = Executors.newFixedThreadPool(slot, new NumberedThreadFactory());
		this.start = System.currentTimeMillis();

		this.magicOutput = new MagicOutput(slot + 1);

		final List<DbFileBeforeRun> allRuns = new ArrayList<>();

		dbCollection.streamsBeforeRun(version).forEach(p -> executorService.submit(() -> {
			try {
				processFile(p);
				allRuns.add(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.HOURS);

		this.magicOutput.restoreOutput();
		Collections.sort(allRuns);

		final List<DbFileAfterRun> allAfter = dbCollection.streamsAfterRun(version).sorted().sequential()
				.collect(Collectors.toList());

		for (int i = 0; i < allRuns.size(); i++) {
			System.out.println("Writing " + i + "/" + allRuns.size());
			System.out.print(Ansi.ansi().cursorUpLine());
			final DbFileBeforeRun prev = i > 0 ? allRuns.get(i - 1) : null;
			final DbFileBeforeRun next = i < allRuns.size() - 1 ? allRuns.get(i + 1) : null;
			allRuns.get(i).createStandaloneHtml(minimalPrefix, prev, next, allRuns, allAfter);
		}
		System.out.print(Ansi.ansi().eraseLine());
		System.out.println("Done!");

		final Path outHtml = Paths.get(version + ".html");

		new HtmlRun(outHtml, dbCollection);

	}

	private void processFile(DbFileBeforeRun dbFile) throws Exception {
		final String threadName = Thread.currentThread().getName();
		final int workerId = Integer.parseInt(threadName.split("-")[1]);

		final long duration = (System.currentTimeMillis() - start) / 1000L;

		magicOutput.updateLivingPart(workerId, workerId + " " + dbFile.getFileName(minimalPrefix));

		final int doneCount = done.get();
		final int remainingCount = count - doneCount;
		final long eta = (doneCount > 0) ? (duration * remainingCount / doneCount) : 0;

		magicOutput.updateLivingPart(slot,
				"Total " + done + "/" + count + " [" + duration + "s ETA=" + (eta / 60) + " minutes ]");
		done.incrementAndGet();

		dbFile.convertMe(this.minimalPrefix);

	}

}
