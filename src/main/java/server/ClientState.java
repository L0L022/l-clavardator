package server;

import java.io.IOException;

import protocol.Event;

public abstract class ClientState {

	protected Client client;

	public ClientState(Client client) {
		this.client = client;
	}

	public ClientState process(Event event) throws IOException {
		return this;
	}

	public ClientState work() throws IOException {
		return this;
	}

	public boolean sendsCommands() {
		return true;
	}

	protected void log(String message) throws IOException {
		System.out.println("[" + client.getRemoteAddress() + "]: " + message);
	}
}
