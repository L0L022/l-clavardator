package protocol.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerConnect extends Command {

	@Override
	public String toString() {
		return "SERVERCONNECT\n";
	}

	private static Pattern pattern;

	public static Command fromString(String str) {
		if (pattern == null) {
			pattern = Pattern.compile("^SERVERCONNECT\n$");
		}

		Matcher m = pattern.matcher(str);
		if (m.matches()) {
			return new ServerConnect();
		}

		return null;
	}
}
