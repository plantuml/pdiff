package com.pdiff;

import java.io.IOException;

import org.fusesource.jansi.AnsiConsole;

import com.beust.jcommander.JCommander;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// Initialiser la console Jansi
		AnsiConsole.systemInstall();

		final InsertCommand insertCommand = new InsertCommand();
		final RunCommand runCommand = new RunCommand();
		final DiffCommand diffCommand = new DiffCommand();

		final JCommander commander = JCommander.newBuilder() //
				.addCommand("insert", insertCommand) //
				.addCommand("run", runCommand) //
				.addCommand("diff", diffCommand).build();

		commander.parse(args);

		String parsedCommand = commander.getParsedCommand();
		if ("insert".equals(parsedCommand)) {
			insertCommand.doit();
		} else if ("run".equals(parsedCommand)) {
			runCommand.doit();
		} else if ("diff".equals(parsedCommand)) {
			diffCommand.doit();
		} else {
			commander.usage();
		}
	}
}