package server;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.ServerConnect;

public class FedWaitConnectState extends ClientState {
	private WaitConnectState state;

	private FedWaitConnectState(WaitConnectState state) {
		super(state.client);
		this.state = state;
	}

	public static ClientState make(Client client) {
		return new FedWaitConnectState((WaitConnectState) WaitConnectState.make(client));
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived() && event.command instanceof ServerConnect) {
			return ConnectedState.makeServer(client);
		}

		return state.process(event);
	}

	@Override
	public ClientState send(Command command) {
		return state.send(command);
	}

	@Override
	public boolean canSend() {
		return state.canSend();
	}

	@Override
	public String name() {
		return "fed wait connect";
	}
}
