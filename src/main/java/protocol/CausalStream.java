package protocol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import protocol.commands.Causal;
import protocol.commands.Command;

public class CausalStream {

	private class CausalWaiting {
		public Causal causal;
		public String from;
		public String to;

		public CausalWaiting(Causal causal, String from, String to) {
			this.causal = causal;
			this.from = from;
			this.to = to;
		}
	}

	private Rec rec;
	private Sent sent;
	private List<CausalWaiting> causalsWainting;
	private Queue<Event> events; // manque FROM; que les reçus ?

	public CausalStream() {
		rec = new Rec();
		sent = new Sent();
		causalsWainting = new ArrayList<CausalWaiting>();
		events = new ArrayDeque<Event>();
	}

	public boolean hasEvent() {
		return !events.isEmpty();
	}

	public Event pollEvent() {
		return events.poll();
	}

	public Causal send(Command command, String from, String to) {
		sent.get(from, to).value += 1;

		Causal causal = new Causal();
		causal.sent = Sent.fromByteArray(sent.toByteArray()); // deep copy
		causal.command = command;

		return causal;
	}

	public void receive(Causal causal, String from, String to) {
		causalsWainting.add(new CausalWaiting(causal, from, to));
		boolean causalsWaintingUpdated = true;

		while (causalsWaintingUpdated) {
			causalsWaintingUpdated = false;

			Iterator<CausalWaiting> it = causalsWainting.iterator();
			while (it.hasNext()) {
				CausalWaiting cw = it.next();
				if (tryDeliver(cw.causal, cw.from, cw.to)) {
					it.remove();
					causalsWaintingUpdated = true;
					break;
				}
			}
		}
	}

	private boolean tryDeliver(Causal causal, String from, String to) {
		if (!canDeliver(causal, from, to)) {
			return false;
		}

		events.add(Event.newReceived(causal.command));

		rec.get(from).value += 1;

		for (Map.Entry<String, Map<String, MutableInteger>> e1 : causal.sent.entrySet()) {
			for (Map.Entry<String, MutableInteger> e2 : e1.getValue().entrySet()) {
				MutableInteger i = sent.get(e1.getKey(), e2.getKey());
				i.value = Math.max(i.value, e2.getValue().value);
			}
		}

		return true;
	}

	private boolean canDeliver(Causal causal, String from, String to) {
		if (rec.get(from).value + 1 != causal.sent.get(from, to).value) {
			return false;
		}

		for (Map.Entry<String, MutableInteger> e : rec.entrySet()) {
			if (!e.getKey().equals(from) && e.getValue().value < causal.sent.get(e.getKey(), to).value) {
				return false;
			}
		}

		return true;
	}
}
