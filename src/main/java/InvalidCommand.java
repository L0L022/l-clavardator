import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvalidCommand extends Command {
	String command;

	InvalidCommand(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return command + "\n";
	}

	static Command fromString(String str) {
		Pattern p = Pattern.compile("^(.*)\n$");
		Matcher m = p.matcher(str);
		while (m.find()) {
			return new InvalidCommand(m.group(1));
		}

		return null;
	}
}
