package server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import protocol.Stream;
import protocol.commands.Command;

public class Client {
	protected Server server;
	protected Stream stream;
	protected String pseudo;

	protected ClientState state;

	public Client(SocketChannel sc, Selector selector, Server server) throws IOException {
		this.server = server;
		stream = new Stream(sc, selector, this);
		pseudo = "";

		state = WaitConnectState.make(this);
	}

	public void work(int ops) {
		try {
			stream.work(ops);

			while (!stream.events.isEmpty()) {
				state = state.process(stream.events.poll());
				assert (state != null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			state = DisconnectedState.makeLogicalError(e.toString(), this);
		}
	}

	public void send(Command command) {
		state = state.send(command);
	}
}
