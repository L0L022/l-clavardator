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

	public static Command fromString(String str) {
		Pattern p = Pattern.compile("^MSG (.*)\n$");
		Matcher m = p.matcher(str);
		while (m.find()) {
			return new Message(m.group(1));
		}

		return null;
	}
}
