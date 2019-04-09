import java.io.IOException;
import java.net.InetSocketAddress;

import server.FedMasterServer;
import server.FedSlaveServer;
import server.P2PServer;
import server.SimpleFedMasterServer;
import server.SimpleFedSlaveServer;
import server.SimpleServer;

public class ServerApp {
	public static void main(String[] args) {
		String server;
		int port;

		if (args.length < 2) {
			System.err.println("usage: ServerApp <server kind> <port>");
			return;
		}

		server = args[0];
		port = Integer.parseInt(args[1]);

		try {
			if (server.equals("SimpleServer")) {
				new SimpleServer(port).start();
			} else if (server.equals("SimpleFedMasterServer")) {
				new SimpleFedMasterServer(port).start();
			} else if (server.equals("SimpleFedSlaveServer")) {
				if (args.length < 4) {
					System.err.println("usage: ServerApp SimpleFedSlaveServer <port> <master address> <master port>");
					return;
				}

				String master = args[2];
				int masterPort = Integer.parseInt(args[3]);

				new SimpleFedSlaveServer(port, new InetSocketAddress(master, masterPort)).start();
			} else if (server.equals("FedMasterServer")) {
				new FedMasterServer(port).start();
			} else if (server.equals("FedSlaveServer")) {
				if (args.length < 4) {
					System.err.println("usage: ServerApp FedSlaveServer <port> <master address> <master port>");
					return;
				}

				String master = args[2];
				int masterPort = Integer.parseInt(args[3]);

				new FedSlaveServer(port, new InetSocketAddress(master, masterPort)).start();
			} else if (server.equals("P2PServer")) {
				P2PServer s = new P2PServer(port);

				for (int i = 2; i + 1 < args.length; i += 2) {
					String peer = args[i];
					int peerPort = Integer.parseInt(args[i + 1]);
					s.connect(new InetSocketAddress(peer, peerPort));
				}

				s.start();
			} else {
				System.err.println("usage: ServerApp <server kind> <port>");
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
