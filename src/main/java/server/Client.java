package server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import protocol.Stream;
import protocol.commands.Command;

public class Client {
	enum Kind {
		Client, Server
	};

	protected SocketChannel socketChannel;
	protected Stream stream;
	protected String pseudo;
	protected Kind kind;
	protected Listener listener;

	protected ClientState state;

	public interface ClientStateMaker {
		ClientState make(Client client);
	}

	public interface Listener {
		void onClosed();

		void onMessageReceived(String message);
	}

	public Client(SocketChannel sc, Selector selector, ClientStateMaker clientStateMaker) throws IOException {
		socketChannel = sc;
		stream = new Stream(sc, selector, this);
		pseudo = "";

		state = clientStateMaker.make(this);
	}

	public void work(int ops) {
		try {
			stream.work(ops);

			while (stream.hasEvent()) {
				state = state.process(stream.pollEvent());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			state = DisconnectedState.make(this);
		}
	}

	public boolean canSend() {
		return state.canSend();
	}

	public void send(Command command) {
		state = state.send(command);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
}
