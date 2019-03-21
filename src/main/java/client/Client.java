package client;

import java.io.IOException;

public class Client {
	public Client() {

	}

	public void start() throws IOException {
		(new Thread(new client.ui.Client())).run();
	}
}
