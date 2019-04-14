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
		Map<String, MutableInteger> m = sent.get(from);

		if (m == null) {
			m = new HashMap<>();
			sent.put(from, m);
		}

		MutableInteger i = m.get(to);

		if (i == null) {
			i = new MutableInteger(0);
			m.put(to, i);
		}

		return i;
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

	public void print() {
		for (Map.Entry<String, Map<String, MutableInteger>> e1 : sent.entrySet()) {
			System.out.println("from: " + e1.getKey());
			for (Map.Entry<String, MutableInteger> e2 : e1.getValue().entrySet()) {
				System.out.println("to: " + e2.getKey() + " = " + e2.getValue());
			}
		}
	}
}