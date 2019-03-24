package server;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.Connect;

public class WaitConnectState extends ClientState {

	private WaitConnectState(Client client) {
		super(client);
		log("wait connect");
	}

	public static ClientState make(Client client) {
		return new WaitConnectState(client);
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived()) {
			if (event.command instanceof Connect) {
				return ConnectedState.make(((Connect) event.command).pseudo, client);
			} else {
				return ErrorConnectState.make(client);
			}
		}

		return DisconnectedState.makeLogicalError("can't process " + event + " in WaitConnectState", client);
	}

	@Override
	public ClientState send(Command command) {
		return DisconnectedState.makeLogicalError("can't send in WaitConnectState", client);
	}
}
