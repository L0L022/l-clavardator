package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class DisconnectedState extends ClientState {

	private DisconnectedState(Client client) {
		super(client);
		log("disconnected");
	}

	private DisconnectedState(String error, Client client) {
		super(client);
		log("disconnected with an " + error);
	}

	private static void disconnect(Client client) {
		try {
			client.stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (client.listener != null) {
			client.listener.onClosed();
		}
	}

	public static ClientState make(Client client) {
		disconnect(client);
		return new DisconnectedState(client);
	}

	public static ClientState makeLogicalError(String error, Client client) {
		disconnect(client);
		return new DisconnectedState("logical error: " + error, client);
	}

	public static ClientState makeProtocolError(String error, Client client) {
		disconnect(client);
		return new DisconnectedState("protocol error: " + error, client);
	}

	@Override
	public ClientState process(Event event) {
		return null;
	}

	@Override
	public ClientState send(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canSend() {
		return false;
	}
}
