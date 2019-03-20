package server.states;

import java.io.IOException;

import protocol.commands.Command;
import server.Client;

public class Disconnect extends ClientState {

	public Disconnect(Client client) throws IOException {
		client.s.close();
		client.server.clients.remove(client);
	}

	@Override
	public ClientState work() throws IOException {
		return null;
	}

	@Override
	public ClientState process(Client self, Command c) throws IOException {
		return null;
	}
}
