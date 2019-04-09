package protocol;

import java.io.Serializable;

public class MutableInteger implements Serializable {
	private static final long serialVersionUID = 1L;
	public int value;

	public MutableInteger(int value) {
		this.value = value;
	}
}