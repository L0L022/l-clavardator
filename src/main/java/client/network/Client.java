package client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import protocol.Event;
import protocol.Stream;
import protocol.commands.Command;
import protocol.commands.Connect;
import protocol.commands.EndOfStream;
import protocol.commands.Message;

public class Client implements Runnable {
	public client.ui.Client uiClient;

	private SocketChannel socketChannel;
	private Selector selector;
	private Stream stream;
	private Listener listener;
	private List<Runnable> toInvokeLater;

	public interface Listener {
		void onClosed();

		void onMessageReceived(String message);
	}

	public Client() throws IOException {
		selector = Selector.open();

		socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("localhost", 1234));
		socketChannel.configureBlocking(false);

		stream = new Stream(socketChannel, selector, null);

		toInvokeLater = Collections.synchronizedList(new ArrayList<Runnable>());
	}

	@Override
	public void run() {
		try {
			stream.send(new Connect("loic"));

			while (true) {
				invokeAll();

				if (!selector.isOpen()) {
					break;
				}

				selector.select();

				for (SelectionKey sk : selector.selectedKeys()) {
					if (sk.isReadable() || sk.isWritable()) {
						stream.work(sk.readyOps());

						while (!stream.events.isEmpty()) {
							process(stream.events.poll());
						}
					}
				}

				selector.selectedKeys().clear();
			}

			invokeAll();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(Command command) throws IOException {
		stream.send(command);
	}

	public void close() throws IOException {
		if (!selector.isOpen()) {
			return;
		}

		socketChannel.close();
		selector.close();

		if (listener != null) {
			listener.onClosed();
		}
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

	private void process(Event event) throws IOException {
		if (event.isReceived() && event.command instanceof Message) {
			String message = ((Message) event.command).message;

			if (listener != null) {
				listener.onMessageReceived(message);
			}
		}

		if (event.isReceived() && event.command instanceof EndOfStream) {
			close();
		}
	}
}
