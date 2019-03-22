package client;

import java.io.IOException;

import protocol.commands.Message;

public class Client {

	public void start() throws IOException {
		client.network.Client networkClient = new client.network.Client();
		client.ui.Client uiClient = new client.ui.Client();

		networkClient.setListener(new client.network.Client.Listener() {

			@Override
			public void onMessageReceived(String message) {
				uiClient.invokeLater(new Runnable() {
					@Override
					public void run() {
						uiClient.addMessage(message);
					}
				});
			}

			@Override
			public void onClosed() {
				uiClient.invokeLater(new Runnable() {
					@Override
					public void run() {
						uiClient.close();
					}
				});
			}
		});

		uiClient.setListener(new client.ui.Client.Listener() {

			@Override
			public void onMessageSent(String message) {
				networkClient.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							networkClient.send(new Message(message));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onClosed() {
				networkClient.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							networkClient.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});

		new Thread(networkClient).start();
		new Thread(uiClient).start();
	}
}
