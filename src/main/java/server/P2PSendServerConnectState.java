package server;

import java.io.IOException;

import protocol.CausalStream;
import protocol.Event;
import protocol.commands.Command;
import protocol.commands.ServerConnect;

public class P2PSendServerConnectState extends ClientState {
	private CausalStream causalStream;
	private Command commandSent;

	private P2PSendServerConnectState(CausalStream causalStream, Command commandSent, Client client) {
		super(client);
		this.causalStream = causalStream;
		this.commandSent = commandSent;
	}

	public static ClientState make(CausalStream causalStream, Client client) {
		try {
			Command commandSent = new ServerConnect();
			client.stream.send(commandSent);
			return new P2PSendServerConnectState(causalStream, commandSent, client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.make(client);
		}
	}

	@Override
	public ClientState send(Command command) {
		logicalError("send not allowed");
		return DisconnectedState.make(client);
	}

	@Override
	public ClientState process(Event event) {
		if (event.isSent() && commandSent.equals(event.command)) {
			return P2PConnectedState.makeServer(causalStream, client);
		}

		logicalError("unexpected event: " + event);
		return DisconnectedState.make(client);
	}

	@Override
	public boolean canSend() {
		return false;
	}

	@Override
	public String name() {
		return "P2P send server connect";
	}

}
