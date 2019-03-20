import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageCommand extends Command {

	String message;

	MessageCommand(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MSG " + message + "\n";
	}

	static Command fromString(String str) {
		Pattern p = Pattern.compile("^MSG (.*)\n$");
		Matcher m = p.matcher(str);
		while (m.find()) {
			return new MessageCommand(m.group(1));
		}

		return null;
	}
}
