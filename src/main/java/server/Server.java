package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import protocol.commands.Message;
import server.Client.Listener;

public class Server {
	final int port = 1234;
	ServerSocketChannel ssc;
	Selector selector;
	Set<Client> clients;

	public Server() throws IOException {
		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		ssc.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));

		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);

		clients = new HashSet<Client>();
	}

	public void start() throws IOException {

		System.out.println("Lancement du serveur sur le port " + port);

		while (true) {
			selector.select();

			for (SelectionKey sk : selector.selectedKeys()) {
				if (sk.isAcceptable()) {
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);

					Client client = new Client(sc, selector);
					client.setListener(new Listener() {

						@Override
						public void onClosed() {
							clients.remove(client);
						}

						@Override
						public void onMessageReceived(String message) {
							for (Client c : clients) {
								if (c.canSend()) {
									c.send(new Message(client.pseudo + "> " + message));
								}
							}
						}

						@Override
						public void onErrorOccured(String error) {
							// TODO Auto-generated method stub

						}

					});
					clients.add(client);
				}

				if (sk.isReadable() || sk.isWritable()) {
					Client c = (Client) sk.attachment();
					c.work(sk.readyOps());
				}
			}
			selector.selectedKeys().clear();
		}

	}
}