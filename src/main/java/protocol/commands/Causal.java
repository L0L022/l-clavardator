package protocol.commands;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protocol.Sent;

public class Causal extends Command {
//	List<String> ids;
//	List<Integer> sent;
	public Sent sent;
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

//		sb.append("|");

		try {
			byte[] bs = sent.toByteArray();
			// byte[] bs_t = new String(bs, "ISO-8859-1").getBytes("ISO-8859-1");
			System.out.println("TAILLE: " + bs.length);
			byte[] size = ByteBuffer.allocate(4).putInt(bs.length).array();
			byte[] command_b = command.toString().getBytes("ISO-8859-1");
			sb.append(new String(size, "ISO-8859-1"));
			byte[] data = ByteBuffer.allocate(bs.length + command_b.length).put(bs).put(command_b).array();
			for (int i = 0; i < data.length; ++i) {
				byte[] t = new byte[1];
				t[0] = data[i];
				System.out.println(i + " " + Integer.toHexString(data[i]) + ": " + new String(t, "ISO-8859-1"));
			}

			sb.append(new String(data, "ISO-8859-1"));

//			if (bs.length != bs_t.length) {
//				System.out.println("pas bonne taille lol");
//			} else {
//				for (int i = 0; i < bs.length; ++i) {
//					System.out.print(Integer.toHexString(bs[i]) + " " + Integer.toHexString(bs_t[i]) + " ");
//
//					if (bs[i] == bs_t[i]) {
//						System.out.println("ok");
//					} else {
//						System.out.println("error");
//					}
//				}
//			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sb.append("| ");

		return sb.toString();
	}

	private static Pattern pattern;

	public static Command fromString(String str) {
		if (pattern == null) {
//			pattern = Pattern.compile("^CAUSAL ([^ ]*) ([^ ]*) (.*)\n$");
//			pattern = Pattern.compile("^CAUSAL \\|(.*)\\| (.*)\n$");
			pattern = Pattern.compile("^CAUSAL (....)([\\s\\S]*)$");
		}
		try {
			Matcher m = pattern.matcher(new String(str.getBytes(), "ISO-8859-1"));
			System.out.println(str);

			if (m.find()) {
//				System.out.println("match: " + m.group(1) + " " + m.group(2));

				byte[] size_b = m.group(1).getBytes("ISO-8859-1");
				int size = ByteBuffer.allocate(4).put(size_b).flip().getInt();

//				System.out.println("bytes received: ");
//
//				for (byte theByte : size_b) {
//					System.out.println(Integer.toHexString(theByte));
//				}

//				System.out.println("taille: " + size);

				byte[] data = m.group(2).getBytes("ISO-8859-1");

				System.out.println(data.length + " " + size);

				if (data.length < size) {
					return null;
				}

				for (int i = 0; i < data.length; ++i) {
					byte[] t = new byte[1];
					t[0] = data[i];
					System.out.println(i + " " + Integer.toHexString(data[i]) + ": " + new String(t, "ISO-8859-1"));
				}

				byte[] clean_data = ByteBuffer.allocate(size).put(ByteBuffer.wrap(data, 0, size)).array();
				byte[] command = ByteBuffer.allocate(data.length - size)
						.put(ByteBuffer.wrap(data, size, data.length - size)).array();

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

				System.out.println("clean_data: " + new String(clean_data, "ISO-8859-1"));
				System.out.println("command: " + new String(command, "ISO-8859-1"));

				Causal causal = new Causal();
				causal.sent = Sent.fromByteArray(clean_data);

				if (causal.sent == null) {
					return null;
				}

				causal.command = Command.fromString(new String(command) + "\n");

				if (causal.command == null) {
					return null;
				}

				return causal;
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		System.out.println("pas match: " + str);

		return null;
	}
}
