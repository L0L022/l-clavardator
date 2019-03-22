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

	public static Command fromString(String str) {
		Pattern p = Pattern.compile("^CONNECT (.*)\n$");
		Matcher m = p.matcher(str);
		while (m.find()) {
			return new Connect(m.group(1));

		}

		return null;
	}
}
