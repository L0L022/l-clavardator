import java.io.IOException;
import java.net.InetSocketAddress;

import client.Client;

public class ClientApp {
	public static void main(String[] args) {
		try {
			String remoteAddress = "localhost";
			int remotePort = 1234;
			String pseudo = "test";

			if (args.length == 3) {
				remoteAddress = args[0];
				remotePort = Integer.parseInt(args[1]);
				pseudo = args[2];
			}

			new Client().start(new InetSocketAddress(remoteAddress, remotePort), pseudo);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
