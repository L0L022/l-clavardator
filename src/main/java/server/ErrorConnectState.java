package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class ErrorConnectState extends ClientState {

	private ErrorConnectState(Client client) {
		super(client);
		log("wait connect error");
	}

	public static ClientState make(Client client) {
		try {
			client.stream.send(new protocol.commands.ConnectError());
			return new ErrorConnectState(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.makeLogicalError(e.toString(), client);
		}
	}

	@Override
	public ClientState process(Event event) {
		if (event.isSent() && event.command instanceof protocol.commands.ConnectError) {
			return DisconnectedState.makeProtocolError("invalid connect", client);
		}

		return this;
	}

	@Override
	public ClientState send(Command command) {
		return DisconnectedState.makeLogicalError("can't send in ErrorConnectState", client);
	}

}
