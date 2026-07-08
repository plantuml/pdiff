package com.pdiff;

import org.fusesource.jansi.AnsiConsole;

import com.beust.jcommander.JCommander;

public class Main {

	public static void main(String[] args) throws Exception {
		AnsiConsole.systemInstall();

		final InsertCommand insertCommand = new InsertCommand();
		final RunCommand runCommand = new RunCommand();
		final DiffCommand diffCommand = new DiffCommand();
		final RenumberCommand renumberCommand = new RenumberCommand();
		final VegaCommand vegaCommand = new VegaCommand();

		final JCommander commander = JCommander.newBuilder() //
				.addCommand("insert", insertCommand) //
				.addCommand("run", runCommand) //
				.addCommand("diff", diffCommand) //
				.addCommand("vega", vegaCommand) //
				.addCommand("renumber", renumberCommand).build();

		commander.parse(args);

		String parsedCommand = commander.getParsedCommand();
		if ("insert".equals(parsedCommand)) {
			insertCommand.doit();
		} else if ("run".equals(parsedCommand)) {
			runCommand.doit();
		} else if ("diff".equals(parsedCommand)) {
			diffCommand.doit();
		} else if ("renumber".equals(parsedCommand)) {
			renumberCommand.doit();
		} else if ("vega".equals(parsedCommand)) {
			vegaCommand.doit();
		} else {
			commander.usage();
		}
	}
}