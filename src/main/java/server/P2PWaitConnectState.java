package server;

import protocol.CausalStream;
import protocol.Event;
import protocol.commands.Command;
import protocol.commands.ServerConnect;

public class P2PWaitConnectState extends WaitConnectState {
	private CausalStream causalStream;

	private P2PWaitConnectState(CausalStream causalStream, Client client) {
		super(client);
		this.causalStream = causalStream;
	}

	public static ClientState make(CausalStream causalStream, Client client) {
		return new P2PWaitConnectState(causalStream, client);
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived() && event.command instanceof ServerConnect) {
			return P2PConnectedState.makeServer(causalStream, client);
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
		return "P2P wait connect";
	}
}
