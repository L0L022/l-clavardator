package client;

import java.io.IOException;
import java.net.InetSocketAddress;

import protocol.commands.Message;

public class Client {

	public void start(InetSocketAddress remoteAddress, String pseudo) throws IOException {
		client.network.Client networkClient = new client.network.Client(remoteAddress, pseudo);
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

			@Override
			public void onErrorOccured(String error) {
				uiClient.invokeLater(new Runnable() {
					@Override
					public void run() {
						uiClient.showError(error);
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
						networkClient.send(new Message(message));
					}
				});
			}

			@Override
			public void onClosed() {
				networkClient.invokeLater(new Runnable() {
					@Override
					public void run() {
						networkClient.close();
					}
				});
			}
		});

		new Thread(networkClient).start();
		new Thread(uiClient).start();
	}
}
