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

@Parameters(commandDescription = "Recalculate the SHA-1 checksums of the dbcollection files if they have been modified.")
public class RenumberCommand {

	public void doit() throws IOException {
		final DbCollection dbCollection = new DbCollection();
		dbCollection.streamsDbFile().forEach(f -> {
			try {
				if (f.isSha1ok() == false) {
					final String newSha1 = dbCollection.rewriteMe(f);
					System.out.println(f.getJsonSha1() + " -> " + newSha1);
					f.deleteMe();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
