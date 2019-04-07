package protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import protocol.commands.Causal;
import protocol.commands.Command;

public class CausalStream {
	private class MutableInteger implements Serializable {
		private static final long serialVersionUID = 1L;
		public int value;

		public MutableInteger(int value) {
			this.value = value;
		}
	}

	private Map<String, MutableInteger> rec;
	private Map<String, Map<String, MutableInteger>> sent;

	public CausalStream() {
		rec = new HashMap<>();
		sent = new HashMap<>();
	}

	void send(Command command, InetSocketAddress from, InetSocketAddress to) throws IOException {
		sent.getOrDefault(from, new HashMap<>()).getOrDefault(to, new MutableInteger(0)).value += 1;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(sent);
		oos.close();

		Causal causal = new Causal();
		causal.data = baos.toString();
		causal.command = command;

		// envoie sur le bon stream
	}

	void receive(Causal causal, InetSocketAddress from, InetSocketAddress to)
			throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(causal.data.getBytes()));
		Map<String, Map<String, MutableInteger>> causal_sent = (Map<String, Map<String, MutableInteger>>) ois
				.readObject();
		ois.close();

		if (recOf(from).value + 1 != getSentOf(causal_sent, from, to).value) {
			// on doit att un autre receive
			return;
		}

		for (Map.Entry<String, MutableInteger> e : rec.entrySet()) {
			if (!e.getKey().equals(from.toString())
					&& e.getValue().value < getSentOf(causal_sent, e.getKey(), to.toString()).value) {
				// on doit att un autre receive
				return;
			}
		}

		// delivery causal

		rec.getOrDefault(from, new MutableInteger(0)).value += 1;

		for (Map.Entry<String, Map<String, MutableInteger>> e1 : causal_sent.entrySet()) {
			for (Map.Entry<String, MutableInteger> e2 : e1.getValue().entrySet()) {
				MutableInteger i = sentOf(e1.getKey(), e2.getKey());
				i.value = Math.max(i.value, e2.getValue().value);
			}
		}
		// recheck la liste des causal en attente
	}

	private MutableInteger recOf(InetSocketAddress from) {
		return recOf(from.toString());
	}

	private MutableInteger recOf(String from) {
		return rec.getOrDefault(from.toString(), new MutableInteger(0));
	}

	private MutableInteger sentOf(String from, String to) {
		return getSentOf(sent, from, to);
	}

	private MutableInteger getSentOf(Map<String, Map<String, MutableInteger>> sent, String from, String to) {
		return sent.getOrDefault(from, new HashMap<>()).getOrDefault(to, new MutableInteger(0));
	}

	private MutableInteger sentOf(InetSocketAddress from, InetSocketAddress to) {
		return getSentOf(sent, from.toString(), to.toString());
	}

	private MutableInteger getSentOf(Map<String, Map<String, MutableInteger>> sent, InetSocketAddress from,
			InetSocketAddress to) {
		return getSentOf(sent, from.toString(), to.toString());
	}
}
