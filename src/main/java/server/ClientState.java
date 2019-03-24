package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public abstract class ClientState {

	protected Client client;

	protected ClientState(Client client) {
		this.client = client;
		log("");
	}

	public abstract ClientState process(Event event);

	public abstract ClientState send(Command command);

	public abstract boolean canSend();

	public abstract String name();

	protected void log(String message) {

		String pseudo = client.pseudo;
		if (pseudo.isEmpty()) {
			pseudo = "no pseudo";
		}

		String address = "no address";
		try {
			address = client.socketChannel.getRemoteAddress().toString();
		} catch (IOException e) {
		}

		System.out.println("[" + client.pseudo + "][" + address + "][" + name() + "]" + message);
	}

	protected void protocolError(String message) {
		log("[protocol error]" + message);
	}

	protected void logicalError(String message) {
		log("[logical error]" + message);
	}
}
