package server.states;

import java.io.IOException;

import protocol.commands.Command;
import server.Client;

public abstract class ClientState {
	public ClientState process(Client self, Command c) throws IOException {
		return this;
	}

	public ClientState work() throws IOException {
		return this;
	}
}
