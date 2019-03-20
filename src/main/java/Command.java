public class Command {
	static Command fromString(String str) {
		System.out.println("fromString: " + str);
		Command c;

		c = MessageCommand.fromString(str);
		System.out.println(c);

		if (c != null) {
			return c;
		}

		c = ConnectCommand.fromString(str);
		System.out.println(c);

		if (c != null) {
			return c;
		}

		c = InvalidCommand.fromString(str);
		System.out.println(c);

		if (c != null) {
			return c;
		}

		return null;
	}
}
