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

	public static Command fromString(String str) {
		Pattern p = Pattern.compile("^(.*)\n$");
		Matcher m = p.matcher(str);
		while (m.find()) {
			return new Invalid(m.group(1));
		}

		return null;
	}
}
