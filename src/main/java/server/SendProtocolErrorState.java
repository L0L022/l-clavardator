package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class SendProtocolErrorState extends ClientState {

	private Command errorCommand;

	private SendProtocolErrorState(Command errorCommand, Client client) {
		super(client);
		this.errorCommand = errorCommand;
	}

	public static ClientState make(Command errorCommand, Client client) {
		try {
			client.stream.send(errorCommand);
			return new SendProtocolErrorState(errorCommand, client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.make(client);
		}
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived()) {
			return this;
		}

		if (event.isSent() && errorCommand.equals(event.command)) {
			return DisconnectedState.make(client);
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
		return "send protocol error";
	}

}
