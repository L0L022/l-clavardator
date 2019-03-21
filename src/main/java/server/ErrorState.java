package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class ErrorState extends ClientState {

	Command c;

	public ErrorState(Client client) throws IOException {
		super(client);
		c = new protocol.commands.Error();
		client.send(c);
		log("error occured");
	}

	@Override
	public boolean sendsCommands() {
		return false;
	}

	@Override
	public ClientState process(Event event) throws IOException {
		if (event.isSent() && event.command instanceof protocol.commands.Error) {
			return new DisconnectState(client);
		}

		return this;
	}

}
