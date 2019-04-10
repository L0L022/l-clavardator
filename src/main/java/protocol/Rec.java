package protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Rec {
	private Map<String, MutableInteger> rec;

	public Rec() {
		rec = new HashMap<>();
	}

	public MutableInteger get(String from) {
		MutableInteger i = rec.get(from);

		if (i == null) {
			i = new MutableInteger(0);
			rec.put(from, i);
		}

		return i;
	}

	public Set<Map.Entry<String, MutableInteger>> entrySet() {
		return rec.entrySet();
	}
}