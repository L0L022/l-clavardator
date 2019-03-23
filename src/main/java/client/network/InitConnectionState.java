package client.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import protocol.Event;
import protocol.Stream;
import protocol.commands.Command;

public class InitConnectionState extends ClientState {

	private InitConnectionState(Client client) {
		super(client);
	}

	public static ClientState make(Client client) {
		try {
			client.selector = Selector.open();

			client.socketChannel = SocketChannel.open();
			client.socketChannel.configureBlocking(false);
			client.socketChannel.register(client.selector, SelectionKey.OP_CONNECT);
			client.socketChannel.connect(client.remoteAddress);

			return new InitConnectionState(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ErrorInitConnectionState.make(e.toString(), client);
		}
	}

	@Override
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

				if (sk.isConnectable()) {
					if (!client.socketChannel.finishConnect()) {
						continue;
					}

					client.stream = new Stream(client.socketChannel, client.selector, null);
					client.state = SendConnectState.make(client);

					return true;
				}
			}

			return true;
		} catch (IOException e) {
			client.state = ErrorInitConnectionState.make(e.toString(), client);
			return true;
		}
	}

	@Override
	public ClientState send(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientState close() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientState processEvent(Event event) {
		// TODO Auto-generated method stub
		return null;
	}
}
