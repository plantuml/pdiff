package com.pdiff;

import java.io.IOException;

import com.beust.jcommander.Parameters;
import com.pdiff.core.DbCollection;

@Parameters(commandDescription = "Recalculate the humhash of the dbcollection files if they have been modified.")
public class RenumberCommand {

	public void doit() throws IOException {
		final DbCollection dbCollection = new DbCollection();
		dbCollection.streamsDbFile().forEach(f -> {
			try {
				if (f.isSha1ok() == false || f.hasHumHash() == false) {
					final String oldName = f.getFileName();
					final String newHumhash = dbCollection.rewriteMe(f);
					System.out.println(oldName + " -> " + newHumhash);
					if (oldName.equals(newHumhash + ".puml") == false)
						f.deleteMe();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
