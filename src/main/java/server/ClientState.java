package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public abstract class ClientState {

	protected Client client;

	protected ClientState(Client client) {
		this.client = client;
		log(null);
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

		String finalMessage = "[" + client.pseudo + "][" + address + "][" + name() + "]";
		if (message != null) {
			finalMessage += ": " + message;
		}

		System.out.println(finalMessage);
	}
}
