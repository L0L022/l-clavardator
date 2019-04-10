package server;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.Connect;

public class WaitConnectState extends ClientState {

	protected WaitConnectState(Client client) {
		super(client);
	}

	public static ClientState make(Client client) {
		return new WaitConnectState(client);
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived()) {
			if (event.command instanceof Connect) {
				return ConnectedState.makeClient(((Connect) event.command).pseudo, client);
			} else {
				protocolError("unexpected event: " + event);
				return SendProtocolErrorState.make(new protocol.commands.ConnectError(), client);
			}
		}

		logicalError("unexpected event: " + event);
		return DisconnectedState.make(client);
	}

	@Override
	public ClientState send(Command command) {
		logicalError("send not allowed");
		return DisconnectedState.make(client);
	}

	@Override
	public boolean canSend() {
		return false;
	}

	@Override
	public String name() {
		return "wait connect";
	}
}
