package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.EndOfStream;
import protocol.commands.Message;

public class ConnectedState extends ClientState {

	String pseudo;

	ConnectedState(String pseudo, Client client) throws IOException {
		super(client);
		this.pseudo = pseudo;
		log(pseudo + " connected");
	}

	@Override
	public ClientState process(Event event) throws IOException {
		if (event.isReceived() && event.command instanceof Message) {
			String message = ((Message) event.command).message;
			log("sent message: " + message);

			for (Client c : client.server.clients) {
				c.send(new Message(pseudo + "> " + message));
			}

			return this;
		}

		if (event.isSent() && event.command instanceof Message) {
			return this;
		}

		if (event.isReceived() && event.command instanceof EndOfStream) {
			return new DisconnectState(client);
		}

		return new ErrorState(client);
	}

}
