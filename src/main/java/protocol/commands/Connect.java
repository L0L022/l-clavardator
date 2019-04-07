package protocol.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connect extends Command {
	public String pseudo;

	public Connect(String pseudo) {
		this.pseudo = pseudo;
	}

	@Override
	public String toString() {
		return "CONNECT " + pseudo + "\n";
	}

	private static Pattern pattern;

	public static Command fromString(String str) {
		if (pattern == null) {
			pattern = Pattern.compile("^CONNECT (.*)\n$");
		}

		Matcher m = pattern.matcher(str);
		if (m.find()) {
			return new Connect(m.group(1));
		}

		return null;
	}
}
