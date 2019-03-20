package server.states;

import java.io.IOException;

import protocol.commands.Command;
import protocol.commands.Connect;
import server.Client;

public class WaitConnect extends ClientState {

	@Override
	public ClientState process(Client self, Command c) throws IOException {
		if (c instanceof Connect) {
			return new Connected(((Connect) c).pseudo);
		}

		return new Error(self);
	}

}
