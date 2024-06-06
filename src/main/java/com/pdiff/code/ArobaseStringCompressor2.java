package com.pdiff.code;

public class ArobaseStringCompressor2 {

	public String compress(String data) {
		return clean2(data);
	}

	public String decompress(String s) {
		return clean2(s);
	}

	private String clean2(String s) {
		// s = s.replace("\0", "");
		s = s.trim();
		// s = s.replace("\r", "").replaceAll("\n+$", "");
		if (s.startsWith("@start")) {
			return s;
		}
		return "@startuml\n" + s + "\n@enduml";
	}

}