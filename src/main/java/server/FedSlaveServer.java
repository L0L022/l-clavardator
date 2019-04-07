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

public class FedSlaveServer {
	int port;
	ServerSocketChannel ssc;
	Selector selector;
	Set<Client> clients;
	Client master;

	public FedSlaveServer(int port, SocketAddress masterAddress) throws IOException {
		this.port = port;

		ssc = ServerSocketChannel.open();
		ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		ssc.bind(new InetSocketAddress(port));
		ssc.configureBlocking(false);

		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);

		clients = new HashSet<Client>();

		SocketChannel masterSocketChannel = SocketChannel.open(masterAddress);
		masterSocketChannel.configureBlocking(false);

		master = new Client(masterSocketChannel, selector, new Client.ClientStateMaker() {

			@Override
			public ClientState make(Client client) {
				return SendServerConnectState.make(client);
			}
		});

		master.setListener(new Listener() {

			@Override
			public void onClosed() {
				clients.remove(master);
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
				if (master.kind == Client.Kind.Client) {
					System.err.println("erreur logique");
				}

				if (master.kind == Client.Kind.Server) {
					for (Client c : clients) {
						if (c.canSend() && c != master) {
							c.send(new Message(message));
						}
					}
				}
			}

		});
		clients.add(master);
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
								if (master.canSend()) {
									master.send(new Message(client.pseudo + "> " + message));
								}
							}

							if (client.kind == Client.Kind.Server) {
								System.err.println("erreur logique");
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