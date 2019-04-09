package protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Sent {
	private Map<String, Map<String, MutableInteger>> sent;

	public Sent() {
		sent = new HashMap<>();
	}

	public MutableInteger get(String from, String to) {
		return sent.getOrDefault(from, new HashMap<>()).getOrDefault(to, new MutableInteger(0));
	}

	public Set<Map.Entry<String, Map<String, MutableInteger>>> entrySet() {
		return sent.entrySet();
	}

	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(sent);
			oos.flush();
			oos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Sent fromByteArray(byte[] bytes) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			Sent sent = new Sent();
			sent.sent = (Map<String, Map<String, MutableInteger>>) ois.readObject();
			ois.close();
			return sent;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}