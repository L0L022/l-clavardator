package server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import protocol.Stream;
import protocol.commands.Command;

public class Client {
	protected SocketChannel socketChannel;
	protected Stream stream;
	protected String pseudo;
	protected Listener listener;

	protected ClientState state;

	public interface Listener {
		void onClosed();

		void onMessageReceived(String message);

		void onErrorOccured(String error);
	}

	public Client(SocketChannel sc, Selector selector) throws IOException {
		socketChannel = sc;
		stream = new Stream(sc, selector, this);
		pseudo = "";

		state = WaitConnectState.make(this);
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
			state = DisconnectedState.makeLogicalError(e.toString(), this);
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
