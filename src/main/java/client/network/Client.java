package client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import protocol.Stream;
import protocol.commands.Command;
import protocol.commands.Connect;

public class Client implements Runnable {
	public client.ui.Client uiClient;

	private SocketChannel socketChannel;
	private Selector selector;
	private Stream stream;

	public Client() throws IOException {
		selector = Selector.open();

		socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("localhost", 1234));
		socketChannel.configureBlocking(false);

		stream = new Stream(socketChannel, selector, null);
	}

	@Override
	public void run() {
		try {
			stream.send(new Connect("loic"));

			while (true) {
				selector.select();

				for (SelectionKey sk : selector.selectedKeys()) {
					if (sk.isReadable() || sk.isWritable()) {
						stream.work(sk.readyOps());

						while (!stream.events.isEmpty()) {
							uiClient.process(stream.events.poll());
						}
					}
				}

				selector.selectedKeys().clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(Command command) throws IOException {
		System.out.println(command);
		stream.send(command);
	}

	public void close() throws IOException {
		stream.close();
	}
}
