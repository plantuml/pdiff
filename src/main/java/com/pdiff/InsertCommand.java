package com.pdiff;

import java.io.IOException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.pdiff.core.DbCollection;
import com.pdiff.core.RawCollection;

@Parameters(commandDescription = "This command allows you to insert new diagrams into the collection.")
public class InsertCommand {

	@Parameter(names = { "-u", "--user" }, description = "Specifies the user who inserts data into the collection", required = false)
	private String user = System.getProperty("user.name");

	public void doit() throws IOException {
		final RawCollection rawCollection = new RawCollection();
		final DbCollection dbCollection = new DbCollection();
		rawCollection.iterateMeAndInsert(user, dbCollection);
	}
}
