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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.pdiff.core.DbCollection;
import com.pdiff.core.DbFileBeforeRun;
import com.pdiff.core.MagicOutput;
import com.pdiff.core.NumberedThreadFactory;
import com.pdiff.html.HtmlRun;

@Parameters(commandDescription = "Run a PlantUML version on the entire collection")
public class RunCommand {

	@Parameter(names = { "-r", "--run" }, description = "Specifies the name of the run", required = false)
	private String version;

	@Parameter(names = { "-f", "--filter" }, description = "Selects only some tests", required = false)
	private String filter;

	@Parameter(names = { "-s", "--slot" }, description = "Specifies the number of parallel slots", required = false)
	private int slot = Runtime.getRuntime().availableProcessors();

	@Parameter(names = { "-p", "--perf" }, description = "Do not generate output files", required = false)
	private boolean onlyPerf = false;


	private AtomicInteger done = new AtomicInteger();

	private int minimalPrefix;

	private MagicOutput magicOutput;
	private RemainingTime remainingTime;

	private Predicate<? super DbFileBeforeRun> getFilter() {
		if (filter == null)
			return p -> true;
		return p -> p.getFileName().startsWith(filter);
	}

	public void doit() throws IOException, InterruptedException {
		
		final DbCollection dbCollection = new DbCollection();
		this.minimalPrefix = dbCollection.getMinimalPrefix();

		if (version == null)
			version = Introspection.versionString();

		System.out.println("");
		System.out.println("Running version " + version);
		System.out.println("");

		final ExecutorService executorService = Executors.newFixedThreadPool(slot, new NumberedThreadFactory());

		this.magicOutput = new MagicOutput(slot + 1);

		final List<DbFileBeforeRun> runOk = new ArrayList<>();

		final List<DbFileBeforeRun> all = dbCollection.streamsBeforeRun(version) //
				.filter(getFilter()) //
				.collect(Collectors.toList());

		remainingTime = RemainingTime.ofTotalCount(all.size());

		for (DbFileBeforeRun p : all) {
			executorService.submit(() -> {
				try {
					processFile(p);
					runOk.add(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.HOURS);

		this.magicOutput.restoreOutput();
		Collections.sort(runOk);
		final Path outHtml = Paths.get(version + ".html");
		new HtmlRun(outHtml, runOk, dbCollection.getMinimalPrefix());

		for (int i = 0; i < runOk.size(); i++) {
			System.out.println("Writing result file " + i + "/" + runOk.size());
			System.out.print(Ansi.ansi().cursorUpLine());
			final DbFileBeforeRun prev = i > 0 ? runOk.get(i - 1) : null;
			final DbFileBeforeRun next = i < runOk.size() - 1 ? runOk.get(i + 1) : null;
			runOk.get(i).createStandaloneHtml(minimalPrefix, prev, next);
		}
		System.out.print(Ansi.ansi().eraseLine());
		System.out.println("Done!");

	}

	private void processFile(DbFileBeforeRun dbFile) throws Exception {
		final String threadName = Thread.currentThread().getName();
		final int workerId = Integer.parseInt(threadName.split("-")[1]);

		magicOutput.updateLivingPart(workerId, workerId + " " + dbFile.getFileName(minimalPrefix));
		magicOutput.updateLivingPart(slot, remainingTime.updateCountAndGetStatus(done.incrementAndGet()));

		dbFile.convertMe(this.minimalPrefix, onlyPerf);
		magicOutput.updateLivingPart(workerId, "");

	}

}
