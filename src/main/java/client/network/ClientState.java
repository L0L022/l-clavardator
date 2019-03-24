package client.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

import protocol.Event;
import protocol.commands.Command;

public abstract class ClientState {
	protected Client client;

	protected ClientState(Client client) {
		this.client = client;
	}

	public boolean run() {
		if (!client.selector.isOpen()) {
			return false;
		}

		try {
			client.selector.select();

			Iterator<SelectionKey> it = client.selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey sk = it.next();
				it.remove();

				if (sk.isReadable() || sk.isWritable()) {
					client.stream.work(sk.readyOps());

					while (client.stream.hasEvent()) {
						client.state = client.state.processEvent(client.stream.pollEvent());
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			client.state = DisconnectedState.make(e.toString(), client);
		}

		return true;
	}

	public abstract ClientState send(Command command);

	public abstract ClientState close();

	public abstract ClientState processEvent(Event event);
}
