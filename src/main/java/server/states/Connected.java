package server.states;

import java.io.IOException;

import protocol.commands.Command;
import protocol.commands.EndOfStream;
import protocol.commands.Message;
import server.Client;

public class Connected extends ClientState {

	String pseudo;

	Connected(String pseudo) {
		this.pseudo = pseudo;
	}

	@Override
	public ClientState process(Client self, Command c) throws IOException {
		if (c instanceof Message) {

			for (Client client : self.server.clients) {
				if (client != self) {
					client.s.send(new Message(pseudo + "> " + ((Message) c).message));
				}
			}

			return this;
		}

		if (c instanceof EndOfStream) {
			return new Disconnect(self);
		}

		return new Error(self);
	}

}
