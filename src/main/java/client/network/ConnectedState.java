package client.network;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.EndOfStream;
import protocol.commands.Message;

public class ConnectedState extends ClientState {

	private ConnectedState(Client client) {
		super(client);
	}

	public static ClientState make(Client client) {
		return new ConnectedState(client);
	}

	@Override
	public ClientState send(Command command) {
		try {
			client.stream.send(command);
			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.make(e.toString(), client);
		}
	}

	@Override
	public ClientState close() {
		return DisconnectedState.make(client);
	}

	@Override
	public ClientState processEvent(Event event) {

		if (event.isReceived() && event.command instanceof Message) {
			String message = ((Message) event.command).message;

			if (client.listener != null) {
				client.listener.onMessageReceived(message);
			}

			return this;
		}

		if (event.isSent() && event.command instanceof Message) {
			return this;
		}

		if (event.isReceived() && event.command instanceof EndOfStream) {
			return close();
		}

		return DisconnectedState.make("can't process " + event + " in ConnectedState", client);
	}
}
