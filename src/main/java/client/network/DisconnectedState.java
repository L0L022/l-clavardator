package client.network;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class DisconnectedState extends ClientState {

	private DisconnectedState(Client client) {
		super(client);
	}

	private DisconnectedState(String error, Client client) {
		super(client);

		if (client.listener != null) {
			client.listener.onErrorOccured(error);
		}
	}

	public static ClientState make(Client client) {
		return new DisconnectedState(client).close();
	}

	public static ClientState make(String error, Client client) {
		return new DisconnectedState(error, client).close();
	}

	@Override
	public ClientState send(Command command) {
		return this;
	}

	@Override
	public ClientState close() {
		if (!client.selector.isOpen()) {
			return this;
		}

		try {
			client.socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			client.selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (client.listener != null) {
			client.listener.onClosed();
		}

		return this;
	}

	@Override
	public ClientState processEvent(Event event) {
		return this;
	}

}
