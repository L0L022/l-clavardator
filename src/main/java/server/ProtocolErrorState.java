package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class ProtocolErrorState extends ClientState {

	private String error;

	private ProtocolErrorState(String error, Client client) {
		super(client);
		this.error = error;
		log("protocol error");
	}

	public static ClientState make(String error, Client client) {
		try {
			client.stream.send(new protocol.commands.Error());
			return new ProtocolErrorState(error, client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.makeLogicalError(e.toString(), client);
		}
	}

	@Override
	public ClientState process(Event event) {
		if (event.isSent() && event.command instanceof protocol.commands.Error) {
			return DisconnectedState.makeProtocolError(error, client);
		}

		return this;
	}

	@Override
	public ClientState send(Command command) {
		return DisconnectedState.makeLogicalError("can't send in ProtocolErrorState", client);
	}

	@Override
	public boolean canSend() {
		return false;
	}

}
