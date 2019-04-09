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
		return rec.getOrDefault(from, new MutableInteger(0));
	}

	public Set<Map.Entry<String, MutableInteger>> entrySet() {
		return rec.entrySet();
	}
}