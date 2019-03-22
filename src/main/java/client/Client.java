package client;

import java.io.IOException;

public class Client {
	public Client() {

	}

	public void start() throws IOException {
		client.network.Client networkClient = new client.network.Client();
		client.ui.Client uiClient = new client.ui.Client();

		networkClient.uiClient = uiClient;
		uiClient.networkClient = networkClient;

		new Thread(networkClient).start();
		new Thread(uiClient).start();
	}
}
