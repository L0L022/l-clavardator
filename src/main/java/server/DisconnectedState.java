package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;

public class DisconnectedState extends ClientState {

	private DisconnectedState(Client client) {
		super(client);
	}

	public static ClientState make(Client client) {
		try {
			client.stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (client.listener != null) {
			client.listener.onClosed();
		}

		return new DisconnectedState(client);
	}

	@Override
	public ClientState process(Event event) {
		return null;
	}

	@Override
	public ClientState send(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canSend() {
		return false;
	}

	@Override
	public String name() {
		return "disconnected";
	}
}
