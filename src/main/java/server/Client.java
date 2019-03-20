package server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import protocol.Stream;
import server.states.ClientState;
import server.states.WaitConnect;

public class Client {
	public Stream s;
	ClientState state;
	Set<Client> clients;

	Client(SocketChannel sc, Selector selector, Set<Client> clients) throws IOException {
		this.s = new Stream(sc, selector, this);
		state = new WaitConnect();
		this.clients = clients;
	}

	void work(int ops) throws IOException {
		s.work(ops);

		while (!s.receivedCommands.isEmpty()) {

//			if (c instanceof Invalid) {
//
//			}
			state = state.process(this, clients, s.receivedCommands.poll());
			assert (state != null);
		}
	}
}
