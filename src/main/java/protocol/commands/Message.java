package protocol.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message extends Command {

	public String message;

	public Message(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MSG " + message + "\n";
	}

	private static Pattern pattern;

	public static Command fromString(String str) {
		if (pattern == null) {
			pattern = Pattern.compile("^MSG (.*)\n$");
		}

		Matcher m = pattern.matcher(str);
		if (m.find()) {
			return new Message(m.group(1));
		}

		return null;
	}
}
