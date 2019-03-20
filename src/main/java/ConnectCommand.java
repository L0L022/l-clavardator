import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectCommand extends Command {
	String pseudo;

	ConnectCommand(String pseudo) {
		this.pseudo = pseudo;
	}

	@Override
	public String toString() {
		return "CONNECT " + pseudo + "\n";
	}

	static Command fromString(String str) {
		Pattern p = Pattern.compile("^CONNECT (.*)\n$");
		Matcher m = p.matcher(str);
		while (m.find()) {
			return new ConnectCommand(m.group(1));

		}

		return null;
	}
}
