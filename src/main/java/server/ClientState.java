package server;

import protocol.Event;
import protocol.commands.Command;

public abstract class ClientState {

	protected Client client;

	protected ClientState(Client client) {
		this.client = client;
	}

	public abstract ClientState process(Event event);

	public abstract ClientState send(Command command);

	protected void log(String message) {
		System.out.println("[" + client + "]: " + message);
	}
}
