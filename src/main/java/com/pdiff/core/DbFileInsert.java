package com.pdiff.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class DbFileInsert {

	private final List<String> content = new ArrayList<>();
	private String url;

	public DbFileInsert() {
	}

	public void append(String s) {
		content.add(s);
	}

	public String getSha1() {
		return SHA1.calculateSHA1(content);
	}

	public List<String> getContent() {
		return Collections.unmodifiableList(content);
	}

	public void exportTo(String user, Path path) throws IOException {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("sha1", getSha1());

		final JsonObject insertion = new JsonObject();
		final Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
		insertion.addProperty("when", DateTimeFormatter.ISO_INSTANT.format(instant));
		if (url != null)
			insertion.addProperty("url", url);
		// insertion.addProperty("os", System.getProperty("os.name"));
		insertion.addProperty("user", user);
		// final String hostName = InetAddress.getLocalHost().getHostName();
		// insertion.addProperty("hostname", hostName);

		jsonObject.add("insertion", insertion);

		final Gson gson = new GsonBuilder().setPrettyPrinting().create();

		final List<String> list2 = List.of(gson.toJson(jsonObject));

		final Stream<String> combinedStream = Stream.concat(list2.stream(), getContent().stream());

		Files.write(path, combinedStream.collect(Collectors.toList()));

	}

	public void setUrl(String url) {
		this.url = url;
	}

}
