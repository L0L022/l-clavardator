package protocol.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invalid extends Command {
	String command;

	Invalid(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return command + "\n";
	}

	private static Pattern pattern;

	public static Command fromString(String str) {
		if (pattern == null) {
			pattern = Pattern.compile("^(.*)\n$");
		}

		Matcher m = pattern.matcher(str);
		if (m.find()) {
			System.out.println("invalide");
			return new Invalid(m.group(1));
		}

		return null;
	}
}
