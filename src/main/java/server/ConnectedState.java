package server;

import java.io.IOException;

import protocol.Event;
import protocol.commands.Command;
import protocol.commands.EndOfStream;
import protocol.commands.Message;

public class ConnectedState extends ClientState {

	protected ConnectedState(Client client) {
		super(client);
	}

	public static ClientState makeClient(String pseudo, Client client) {
		client.pseudo = pseudo;
		client.kind = Client.Kind.Client;
		return new ConnectedState(client);
	}

	public static ClientState makeServer(Client client) {
		client.kind = Client.Kind.Server;
		return new ConnectedState(client);
	}

	@Override
	public ClientState process(Event event) {
		System.out.println("normal process: " + event);

		if (event.isReceived()) {
			if (event.command instanceof Message) {
				String message = ((Message) event.command).message;
				log("[sent message][" + message + "]");

				if (client.listener != null) {
					client.listener.onMessageReceived(message);
				}

				return this;
			}

			if (event.command instanceof EndOfStream) {
				return DisconnectedState.make(client);
			}

			protocolError("unexpected event: " + event);
			return SendProtocolErrorState.make(new protocol.commands.Error(), client);
		}

		if (event.isSent() && event.command instanceof Message) {
			return this;
		}

		logicalError("unexpected event: " + event);
		return DisconnectedState.make(client);
	}

	@Override
	public ClientState send(Command command) {
		try {
			client.stream.send(command);
			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logicalError(e.toString());
			return DisconnectedState.make(client);
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
