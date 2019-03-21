package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Connect;

public class WaitConnectState extends ClientState {

	public WaitConnectState(Client client) throws IOException {
		super(client);
		log("wait connect");
	}

	@Override
	public boolean sendsCommands() {
		return false;
	}

	@Override
	public ClientState process(Event event) throws IOException {
		if (event.isReceived() && event.command instanceof Connect) {
			return new ConnectedState(((Connect) event.command).pseudo, client);
		}

		return new ErrorState(client);
	}

}
