package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class SendProtocolErrorState extends ClientState {

	private Command errorCommand;
	private String errorMessage;

	private SendProtocolErrorState(Command errorCommand, String errorMessage, Client client) {
		super(client);
		this.errorCommand = errorCommand;
		this.errorMessage = errorMessage;
	}

	public static ClientState make(Command errorCommand, String errorMessage, Client client) {
		try {
			client.stream.send(errorCommand);
			return new SendProtocolErrorState(errorCommand, errorMessage, client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.makeLogicalError(e.toString(), client);
		}
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived()) {
			return this;
		}

		if (event.isSent() && errorCommand.equals(event.command)) {
			return DisconnectedState.makeProtocolError(errorMessage, client);
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
		return "send protocol error";
	}

}
