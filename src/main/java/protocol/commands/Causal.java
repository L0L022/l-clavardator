package protocol.commands;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import protocol.Sent;

public class Causal extends Command {
	public Sent sent;
	public Command command;

	@Override
	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bytes.write(new String("CAUSAL ").getBytes("ISO-8859-1"));

			byte[] bs = sent.toByteArray();
			byte[] size = ByteBuffer.allocate(4).putInt(bs.length).array();
			byte[] command_b = command.toByteArray();
			bytes.write(size);
			byte[] data = ByteBuffer.allocate(bs.length + command_b.length).put(bs).put(command_b).array();
			bytes.write(data);

			return bytes.toByteArray();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Command fromByteArray(byte[] bytes) {
		try {
			String str = new String(bytes, "ISO-8859-1");
			if (!str.startsWith("CAUSAL ")) {
				return null;
			}

			if (bytes.length < 7 + 4) {
				return null;
			}

			byte[] size_b = new byte[4];
			for (int i = 0; i < size_b.length; ++i) {
				size_b[i] = bytes[i + 7];
			}
			int size = ByteBuffer.allocate(4).put(size_b).flip().getInt();

			byte[] data = new byte[bytes.length - 7 - 4];
			for (int i = 0; i < data.length; ++i) {
				data[i] = bytes[i + 7 + 4];
			}

			if (data.length < size) {
				return null;
			}

			byte[] clean_data = ByteBuffer.allocate(size).put(ByteBuffer.wrap(data, 0, size)).array();
			byte[] command = ByteBuffer.allocate(data.length - size)
					.put(ByteBuffer.wrap(data, size, data.length - size)).array();

			Causal causal = new Causal();
			causal.sent = Sent.fromByteArray(clean_data);

			if (causal.sent == null) {
				return null;
			}

			causal.command = Command.fromByteArray(command);

			if (causal.command == null) {
				return null;
			}

			return causal;

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}
}
