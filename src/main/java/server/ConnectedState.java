package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.EndOfStream;
import protocol.commands.Message;

public class ConnectedState extends ClientState {
	private ConnectedState(Client client) {
		super(client);
	}

	public static ClientState make(String pseudo, Client client) {
		client.pseudo = pseudo;
		return new ConnectedState(client);
	}

	@Override
	public ClientState process(Event event) {
		if (event.isReceived()) {
			if (event.command instanceof Message) {
				String message = ((Message) event.command).message;
				log("sent message: " + message);

				if (client.listener != null) {
					client.listener.onMessageReceived(message);
				}

				return this;
			}

			if (event.command instanceof EndOfStream) {
				return DisconnectedState.make(client);
			}

			return SendProtocolErrorState.make(new protocol.commands.Error(), "unexpected event: " + event, client);
		}

		if (event.isSent() && event.command instanceof Message) {
			return this;
		}

		return DisconnectedState.makeLogicalError("unexpected event: " + event, client);
	}

	@Override
	public ClientState send(Command command) {
		try {
			client.stream.send(command);
			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DisconnectedState.makeLogicalError(e.toString(), client);
		}
	}

	@Override
	public boolean canSend() {
		return true;
	}

	@Override
	public String name() {
		return "connected";
	}

}
