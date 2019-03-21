package server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import protocol.Stream;
import protocol.commands.Command;

public class Client {
	Stream stream;
	ClientState state;
	Server server;

	public Client(SocketChannel sc, Selector selector, Server server) throws IOException {
		this.stream = new Stream(sc, selector, this);
		state = new WaitConnectState(this);
		this.server = server;
	}

	public void work(int ops) throws IOException {
		stream.work(ops);
		state.work();

		while (!stream.events.isEmpty()) {
			state = state.process(stream.events.poll());
			assert (state != null);
		}
	}

	public void send(Command command) throws IOException {
		if (state.sendsCommands()) {
			stream.send(command);
		}
	}

	SocketAddress getRemoteAddress() throws IOException {
		return stream.sc.getRemoteAddress();
	}
}
