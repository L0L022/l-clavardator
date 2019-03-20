package server.states;

import java.util.Set;

import protocol.commands.Command;
import protocol.commands.Connect;
import server.Client;

public class WaitConnect implements ClientState {

	public ClientState process(Client self, Set<Client> clients, Command c) {
		if (c instanceof Connect) {
			return new Connected(((Connect) c).pseudo);
		}

		return null;
	}

}
