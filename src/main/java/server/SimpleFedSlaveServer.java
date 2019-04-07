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
import java.util.Iterator;
import java.util.Set;

import protocol.commands.Message;
import server.Client.Listener;

public class SimpleFedSlaveServer {
	int port;
	ServerSocketChannel ssc;
	Selector selector;
	Set<Client> clients;

	public SimpleFedSlaveServer(int port, SocketAddress masterAddress) throws IOException {
		this.port = port;

		ssc = ServerSocketChannel.open();
		ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		ssc.bind(new InetSocketAddress(port));
		ssc.configureBlocking(false);

		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);

		clients = new HashSet<Client>();

		SocketChannel master = SocketChannel.open(masterAddress);
		master.configureBlocking(false);

		Client client = new Client(master, selector, new Client.ClientStateMaker() {

			@Override
			public ClientState make(Client client) {
				return SendServerConnectState.make(client);
			}
		});

		client.setListener(new Listener() {

			@Override
			public void onClosed() {
				clients.remove(client);
				try {
					selector.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Serveur maître déconnecté. Fermeture du serveur...");
			}

			@Override
			public void onMessageReceived(String message) {
				if (client.kind == Client.Kind.Client) {
					for (Client c : clients) {
						if (c.canSend()) {
							c.send(new Message(client.pseudo + "> " + message));
						}
					}
				}

				if (client.kind == Client.Kind.Server) {
					for (Client c : clients) {
						if (c.canSend() && c != client) {
							c.send(new Message(message));
						}
					}
				}
			}

		});
		clients.add(client);
	}

	public void start() throws IOException {

		System.out.println("Lancement du serveur sur le port " + port);

		while (selector.isOpen()) {
			selector.select();

			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey sk = it.next();
				it.remove();

				if (sk.isAcceptable()) {
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);

					Client client = new Client(sc, selector, new Client.ClientStateMaker() {

						@Override
						public ClientState make(Client client) {
							return WaitConnectState.make(client);
						}
					});

					client.setListener(new Listener() {

						@Override
						public void onClosed() {
							clients.remove(client);
						}

						@Override
						public void onMessageReceived(String message) {
							if (client.kind == Client.Kind.Client) {
								for (Client c : clients) {
									if (c.canSend()) {
										c.send(new Message(client.pseudo + "> " + message));
									}
								}
							}

							if (client.kind == Client.Kind.Server) {
								for (Client c : clients) {
									if (c.canSend() && c != client) {
										c.send(new Message(message));
									}
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
		}

	}
}