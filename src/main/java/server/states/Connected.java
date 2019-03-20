package server.states;

import java.io.IOException;
import java.util.Set;

import protocol.commands.Command;
import protocol.commands.Message;
import server.Client;

public class Connected implements ClientState {

	String pseudo;

	Connected(String pseudo) {
		this.pseudo = pseudo;
	}

	public ClientState process(Client self, Set<Client> clients, Command c) throws IOException {
		if (c instanceof Message) {

			for (Client client : clients) {
				// if (client != self) {
				client.s.send(c);
//					System.out.println("sent: " + c);
//				}
			}

			return this;
		}

		return null;
	}

}
