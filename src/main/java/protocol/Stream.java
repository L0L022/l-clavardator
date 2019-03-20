package protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

import protocol.commands.Command;

public class Stream {
	SocketChannel sc;
	String readLine;
	ByteBuffer readBuffer;
	ByteBuffer writeBuffer;
	public Queue<Command> receivedCommands;
	public Queue<Command> commandsToSend;
	Selector selector;
	SelectionKey writeKey;
	Object keyAttachement;
	boolean keyiswrite;

	public Stream(SocketChannel sc, Selector selector, Object keyAttachement) throws IOException {
		this.sc = sc;
		readLine = "";
		readBuffer = ByteBuffer.allocate(128);
		receivedCommands = new ArrayDeque<Command>();
		commandsToSend = new ArrayDeque<Command>();
		this.selector = selector;
		this.keyAttachement = keyAttachement;

		writeKey = this.sc.register(this.selector, SelectionKey.OP_READ, this.keyAttachement);

		keyiswrite = false;
	}

	public void send(Command c) throws IOException {
		commandsToSend.add(c);

		if (!keyiswrite) {
			keyiswrite = true;
			writeKey = sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, keyAttachement);
		}
	}

	public void work(int ops) throws IOException {

		if ((ops & SelectionKey.OP_READ) != 0) {
			System.out.println("READ");
			read();
		}

		if ((ops & SelectionKey.OP_WRITE) != 0) {
			System.out.println("WRITE");
			write();
		}
	}

	private void read() throws IOException {
		int byte_read = sc.read(readBuffer);

		if (byte_read == -1) {
			// fin de connexion
		}

		System.out.println(byte_read);
		readBuffer.flip();

		// System.out.println(d.buffer);
		while (readBuffer.hasRemaining()) {
			char c = (char) readBuffer.get();
			readLine += c;
			// System.out.println("get: "+c);
			Command command = Command.fromString(readLine);
			if (command != null) {
				readLine = "";
				receivedCommands.add(command);
				System.out.println(receivedCommands);
			}
		}

		readBuffer.clear();
	}

	private void write() throws IOException {
		if (writeBuffer != null) {
			sc.write(writeBuffer);

			if (!writeBuffer.hasRemaining()) {
				writeBuffer = null;
			}
		}

		if (writeBuffer == null && !commandsToSend.isEmpty()) {
			Command c = commandsToSend.poll();
			writeBuffer = ByteBuffer.wrap(c.toString().getBytes()); // utiliser le buff prec
		} else {
//			if (writeKey.isValid()) {
//				writeKey.cancel();
//			}
			writeKey = sc.register(selector, SelectionKey.OP_READ, keyAttachement);
			keyiswrite = false;
			// System.out.println("plus écrire");
		}
	}
}
