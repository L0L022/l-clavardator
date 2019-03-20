package server.states;

import java.io.IOException;

import protocol.commands.Command;
import server.Client;

public class Error extends ClientState {

	Command c;
	Client client;

	public Error(Client client) throws IOException {
		this.client = client;
		c = new protocol.commands.Error();
		client.s.send(c);
		System.out.println("erreur avec un client");
	}

	@Override
	public ClientState work() throws IOException {
		if (client.s.doneSending(c)) {
			return new Disconnect(client);
		}

		return this;
	}

}
