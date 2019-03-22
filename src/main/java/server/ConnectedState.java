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
			log("sent message: " + event.command);

			for (Client c : client.server.clients) {
//				if (c != client) {
				c.send(new Message(pseudo + "> " + ((Message) event.command).message));
//				}
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
