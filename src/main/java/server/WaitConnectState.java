package server;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.Connect;

public class WaitConnectState extends ClientState {

	private WaitConnectState(Client client) {
		super(client);
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
				return SendProtocolErrorState.make(new protocol.commands.ConnectError(), "connect error", client);
			}
		}

		return DisconnectedState.makeLogicalError("unexpected event: " + event, client);
	}

	@Override
	public ClientState send(Command command) {
		return DisconnectedState.makeLogicalError("send not allowed", client);
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
