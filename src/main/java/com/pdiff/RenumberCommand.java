package com.pdiff;

import java.io.IOException;

import com.beust.jcommander.Parameters;
import com.pdiff.core.DbCollection;

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
