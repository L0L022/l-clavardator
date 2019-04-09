package server;

import java.io.IOException;

import protocol.CausalStream;
import protocol.Event;
import protocol.commands.Causal;
import protocol.commands.Command;

public class P2PConnectedState extends ConnectedState {
	private CausalStream causalStream;

	private P2PConnectedState(CausalStream causalStream, Client client) {
		super(client);
		this.causalStream = causalStream;
	}

	public static ClientState makeServer(CausalStream causalStream, Client client) {
		client.kind = Client.Kind.Server; // copié
		return new P2PConnectedState(causalStream, client);
	}

	@Override
	public ClientState process(Event event) {
		log("process: " + event);
		if (event.isSent() && event.command instanceof Causal) {
			return this;
		}

		if (event.isReceived() && event.command instanceof Causal) {
			try {
				String from = client.socketChannel.getRemoteAddress().toString();
				String to = client.socketChannel.getLocalAddress().toString();
				causalStream.receive((Causal) event.command, from, to);
				System.out.println("received causal: " + event);
				return this;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logicalError(e.toString());
				return DisconnectedState.make(client);
			}
		}

		return super.process(event);
	}

	@Override
	public ClientState send(Command command) {
		if (client.kind == Client.Kind.Server) {
			try {
				String from = client.socketChannel.getLocalAddress().toString();
				String to = client.socketChannel.getRemoteAddress().toString();
				command = causalStream.send(command, from, to);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logicalError(e.toString());
				return DisconnectedState.make(client);
			}
		}
		return super.send(command);
	}

	@Override
	public boolean canSend() {
		return super.canSend();
	}

	@Override
	public String name() {
		return "P2P connected";
	}

}
