package client.network;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.Connect;

public class SendConnectState extends ClientState {

	private SendConnectState(Client client) {
		super(client);
	}

	public static ClientState make(Client client) {
		try {
			client.stream.send(new Connect(client.pseudo));
			return new SendConnectState(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.make(e.toString(), client);
		}
	}

	@Override
	public ClientState send(Command command) {
		return DisconnectedState.make("can't send in InitConnectionState", client);
	}

	@Override
	public ClientState close() {
		return DisconnectedState.make("can't close in InitConnectionState", client);
	}

	@Override
	public ClientState processEvent(Event event) {
		if (event.isSent() && event.command instanceof Connect) {
			return ConnectedState.make(client);
		}

		return DisconnectedState.make("can't process " + event + " in InitConnectionState", client);
	}

}
