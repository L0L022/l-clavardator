package server;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.ServerConnect;

public class FedWaitConnectState extends WaitConnectState {

	private FedWaitConnectState(Client client) {
		super(client);
	}

	public static ClientState make(Client client) {
		return new FedWaitConnectState(client);
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived() && event.command instanceof ServerConnect) {
			return ConnectedState.makeServer(client);
		}

		return super.process(event);
	}

	@Override
	public ClientState send(Command command) {
		return super.send(command);
	}

	@Override
	public boolean canSend() {
		return super.canSend();
	}

	@Override
	public String name() {
		return "fed wait connect";
	}
}
