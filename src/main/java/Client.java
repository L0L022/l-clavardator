import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Client {
	Stream s;
	State state;

	enum State {
		WaitConnection, Connected, Disconnected,
	}

	Client(SocketChannel sc) {
		this.s = new Stream(sc);
		state = State.WaitConnection;
	}

	void work(int ops) throws IOException {
		s.work(ops);
	}
}
