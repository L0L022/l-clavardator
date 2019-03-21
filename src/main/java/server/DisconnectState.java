package server;

import java.io.IOException;

import protocol.Event;

public class DisconnectState extends ClientState {

	public DisconnectState(Client client) throws IOException {
		super(client);
		log("disconnected");
		client.stream.close();
		client.server.clients.remove(client);
	}

	@Override
	public boolean sendsCommands() {
		return false;
	}

	@Override
	public ClientState work() throws IOException {
		return null;
	}

	@Override
	public ClientState process(Event event) throws IOException {
		return null;
	}
}
