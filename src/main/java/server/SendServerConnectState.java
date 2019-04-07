package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.ServerConnect;

public class SendServerConnectState extends ClientState {

	private Command commandSent;

	private SendServerConnectState(Command commandSent, Client client) {
		super(client);
		this.commandSent = commandSent;
	}

	public static ClientState make(Client client) {
		try {
			Command commandSent = new ServerConnect();
			client.stream.send(commandSent);
			return new SendServerConnectState(commandSent, client);
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
			return ConnectedState.makeServer(client);
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
		return "send server connect";
	}

}
