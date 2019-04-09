package server;

import protocol.CausalStream;
import protocol.Event;
import protocol.commands.Command;
import protocol.commands.ServerConnect;

public class P2PWaitConnectState extends ClientState {
	private WaitConnectState state;
	private CausalStream causalStream;

	private P2PWaitConnectState(CausalStream causalStream, WaitConnectState state) {
		super(state.client);
		this.causalStream = causalStream;
		this.state = state;
	}

	public static ClientState make(CausalStream causalStream, Client client) {
		return new P2PWaitConnectState(causalStream, (WaitConnectState) WaitConnectState.make(client));
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived() && event.command instanceof ServerConnect) {
			return P2PConnectedState.makeServer(causalStream, client);
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
		return "P2P wait connect";
	}
}
