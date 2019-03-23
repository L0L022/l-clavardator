package client.network;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import protocol.Stream;
import protocol.commands.Command;

public class Client implements Runnable {
	protected SocketChannel socketChannel;
	protected Selector selector;
	protected Stream stream;
	protected Listener listener;

	private List<Runnable> toInvokeLater;
	protected ClientState state;

	protected String pseudo;
	protected InetSocketAddress remoteAddress;

	public interface Listener {
		void onClosed();

		void onMessageReceived(String message);

		void onErrorOccured(String error);
	}

	public Client(InetSocketAddress remoteAddress, String pseudo) {
		this.pseudo = pseudo;
		this.remoteAddress = remoteAddress;
		toInvokeLater = Collections.synchronizedList(new ArrayList<Runnable>());
	}

	@Override
	public void run() {
		state = InitConnectionState.make(this);

		while (state.run()) {
			invokeAll();
		}

		invokeAll();
	}

	public void send(Command command) {
		state = state.send(command);
	}

	public void close() {
		state = state.close();
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void invokeLater(Runnable runnable) {
		toInvokeLater.add(runnable);
		selector.wakeup();
	}

	private void invokeAll() {
		Iterator<Runnable> it = toInvokeLater.iterator();

		while (it.hasNext()) {
			it.next().run();
			it.remove();
		}
	}
}
