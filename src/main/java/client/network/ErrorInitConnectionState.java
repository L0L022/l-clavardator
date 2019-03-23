package client.network;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class ErrorInitConnectionState extends ClientState {

	private ErrorInitConnectionState(String error, Client client) {
		super(client);
	}

	public static ClientState make(String error, Client client) {
		if (client.socketChannel != null) {
			try {
				client.socketChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (client.selector != null) {
			try {
				client.selector.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (client.listener != null) {
			client.listener.onErrorOccured(error);
		}

		if (client.listener != null) {
			client.listener.onClosed();
		}

		return new ErrorInitConnectionState(error, client);
	}

	@Override
	public ClientState send(Command command) {
		return this;
	}

	@Override
	public ClientState close() {
		return this;
	}

	@Override
	public ClientState processEvent(Event event) {
		return this;
	}

}
