package protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

import protocol.commands.Command;
import protocol.commands.EndOfStream;

public class Stream {

	public SocketChannel sc;
	Selector selector;
	SelectionKey key;
	Object keyAttachement;

	ByteArrayOutputStream readLine;
	ByteBuffer readBuffer;
	ByteBuffer writeBuffer;
	Command commandWriting;

	Queue<Command> commandsToSend;
	public Queue<Event> events;

	public Stream(SocketChannel sc, Selector selector, Object keyAttachement) throws IOException {
		this.sc = sc;
		readLine = new ByteArrayOutputStream(128);
		readBuffer = ByteBuffer.allocate(128);
		commandsToSend = new ArrayDeque<Command>();
		events = new ArrayDeque<Event>();
		this.selector = selector;
		this.keyAttachement = keyAttachement;

		key = this.sc.register(this.selector, SelectionKey.OP_READ, this.keyAttachement);
	}

	public void send(Command c) throws IOException {
		commandsToSend.add(c);

		if ((key.interestOps() & SelectionKey.OP_WRITE) == 0) {
			key = sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, keyAttachement);
		}
	}

	public void work(int ops) throws IOException {

		if ((ops & SelectionKey.OP_READ) != 0) {
			read();
		}

		if ((ops & SelectionKey.OP_WRITE) != 0) {
			write();
		}
	}

	public void close() throws IOException {
		sc.close();
	}

	private void read() throws IOException {
		int byte_read = sc.read(readBuffer);

		if (byte_read == -1) {
			events.add(Event.newReceived(new EndOfStream()));
			return;
		}

		readBuffer.flip();

		while (readBuffer.hasRemaining()) {
			readLine.write(readBuffer.get());

			Command command = Command.fromString(readLine.toString());
			if (command != null) {
				readLine.reset();
				events.add(Event.newReceived(command));
			}
		}

		readBuffer.clear();
	}

	private void write() throws IOException {
		if (writeBuffer != null) {
			sc.write(writeBuffer);

			if (writeBuffer.hasRemaining()) {
				return;
			}
		}

		if (commandWriting != null) {
			events.add(Event.newSent(commandWriting));
		}

		if (commandsToSend.isEmpty()) {
			key = sc.register(selector, SelectionKey.OP_READ, keyAttachement);
			commandWriting = null;
		} else {
			Command c = commandsToSend.poll();
			writeBuffer = ByteBuffer.wrap(c.toString().getBytes());
			commandWriting = c;
		}
	}
}
