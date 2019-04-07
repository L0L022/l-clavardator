package protocol.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerConnect extends Command {

	@Override
	public String toString() {
		return "SERVERCONNECT\n";
	}

	public static Command fromString(String str) {
		Pattern p = Pattern.compile("^SERVERCONNECT\n$");
		Matcher m = p.matcher(str);

		if (m.matches()) {
			return new ServerConnect();
		}

		return null;
	}
}
