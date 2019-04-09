package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import protocol.CausalStream;
import protocol.Event;
import protocol.commands.Message;
import server.Client.Listener;

public class P2PServer {
	int port;
	ServerSocketChannel ssc;
	Selector selector;
	Set<Client> clients;
	CausalStream causalStream;

	public P2PServer(int port) throws IOException {
		this.port = port;

		ssc = ServerSocketChannel.open();
		ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		ssc.bind(new InetSocketAddress(port));
		ssc.configureBlocking(false);

		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);

		clients = new HashSet<Client>();

		causalStream = new CausalStream();
	}

	public void connect(SocketAddress serverAddress) throws IOException {
		SocketChannel sc = SocketChannel.open(serverAddress);
		sc.configureBlocking(false);

		Client client = new Client(sc, selector, new Client.ClientStateMaker() {

			@Override
			public ClientState make(Client client) {
				return P2PSendServerConnectState.make(causalStream, client);
			}
		});

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

		});

		clients.add(client);
	}

	public void start() throws IOException {

		System.out.println("Lancement du serveur sur le port " + port);

		while (true) {
			selector.select();

			for (SelectionKey sk : selector.selectedKeys()) {
				if (sk.isAcceptable()) {
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);

					Client client = new Client(sc, selector, new Client.ClientStateMaker() {

						@Override
						public ClientState make(Client client) {
							return P2PWaitConnectState.make(causalStream, client);
						}
					});

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

					});

					clients.add(client);
				}

				if (sk.isReadable() || sk.isWritable()) {
					Client c = (Client) sk.attachment();
					c.work(sk.readyOps());
				}
			}
			selector.selectedKeys().clear();

			while (causalStream.hasEvent()) {
				Event event = causalStream.pollEvent();
				System.out.println("causal poll: " + event);
				if (event.isReceived() && event.command instanceof Message) {
					for (Client c : clients) {
						if (c.kind == Client.Kind.Client && c.canSend()) {
							c.send(event.command);
						}
					}
				}

				if (event.isSent()) {
					// pas possible pour le moment
				}
			}
		}

	}
}