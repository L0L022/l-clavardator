package protocol.commands;

public abstract class Command {
	public static Command fromByteArray(byte[] bytes) {
		Command c;
		String str = new String(bytes);

		c = Causal.fromByteArray(bytes);
		if (c != null) {
			return c;
		}
		if (str.startsWith("CAUSAL")) {
			return null;
		}

		c = Message.fromString(str);
		if (c != null) {
			return c;
		}

		c = Connect.fromString(str);
		if (c != null) {
			return c;
		}

		c = ServerConnect.fromString(str);
		if (c != null) {
			return c;
		}

		c = Invalid.fromString(str);
		if (c != null) {
			return c;
		}

		return null;
	}

	public byte[] toByteArray() {
		return toString().getBytes();
	}
}
