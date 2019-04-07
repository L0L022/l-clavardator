package protocol.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Causal extends Command {
//	List<String> ids;
//	List<Integer> sent;
	public String data;
	public Command command;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CAUSAL ");

//		for (int i = 0; i < ids.size(); ++i) {
//			if (i > 0) {
//				sb.append("|");
//			}
//			sb.append(ids.get(i));
//		}
//		sb.append(" ");
//
//		for (int i = 0; i < sent.size(); ++i) {
//			if (i > 0) {
//				sb.append("|");
//			}
//			sb.append(sent.get(i));
//		}
//		sb.append(" ");

		sb.append("|");
		sb.append(data);
		sb.append("| ");

		sb.append(command.toString());
		return sb.toString();
	}

	private static Pattern pattern;

	public static Command fromString(String str) {
		if (pattern == null) {
//			pattern = Pattern.compile("^CAUSAL ([^ ]*) ([^ ]*) (.*)\n$");
			pattern = Pattern.compile("^CAUSAL \\|(.*)\\| (.*)\n$");
		}

		Matcher m = pattern.matcher(str);
		if (m.find()) {
//			String idsStr = m.group(1);
//			String sentStr = m.group(2);
//			String commandStr = m.group(3);
//
//			Causal causal = new Causal();
//			causal.ids = Arrays.asList(idsStr.split("|"));
//
//			String[] sent = sentStr.split("|");
//			causal.sent = new ArrayList<Integer>(sent.length);
//			for (String n : sent) {
//				causal.sent.add(Integer.parseInt(n));
//			}
//
//			causal.command = Command.fromString(commandStr + "\n");

			Causal causal = new Causal();
			causal.data = m.group(1);
			causal.command = Command.fromString(m.group(2) + "\n");

			return causal;
		}

		return null;
	}
}
