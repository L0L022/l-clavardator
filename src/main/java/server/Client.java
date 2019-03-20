package server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import protocol.Stream;
import server.states.ClientState;
import server.states.WaitConnect;

public class Client {
	public Stream s;
	ClientState state;
	public Server server;

	Client(SocketChannel sc, Selector selector, Server server) throws IOException {
		this.s = new Stream(sc, selector, this);
		state = new WaitConnect();
		this.server = server;
	}

	void work(int ops) throws IOException {
		s.work(ops);
		state.work();

		while (!s.receivedCommands.isEmpty()) {
			state = state.process(this, s.receivedCommands.poll());
			assert (state != null);
		}
	}
}
