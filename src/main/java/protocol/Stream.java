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

	private SocketChannel socketChannel;
	private Selector selector;
	private Object keyAttachement;
	private SelectionKey key;

	private ByteArrayOutputStream readLine;
	private ByteBuffer readBuffer;

	private ByteBuffer writeBuffer;
	private Command commandWriting;
	private Queue<Command> commandsToSend;

	private Queue<Event> events;

	public Stream(SocketChannel socketChannel, Selector selector, Object keyAttachement) throws IOException {
		this.socketChannel = socketChannel;
		this.selector = selector;
		this.keyAttachement = keyAttachement;
		key = this.socketChannel.register(this.selector, SelectionKey.OP_READ, this.keyAttachement);

		readLine = new ByteArrayOutputStream(128);
		readBuffer = ByteBuffer.allocate(128);

		writeBuffer = null;
		commandWriting = null;
		commandsToSend = new ArrayDeque<Command>();

		events = new ArrayDeque<Event>();
	}

	public void send(Command command) throws IOException {
		commandsToSend.add(command);

		if ((key.interestOps() & SelectionKey.OP_WRITE) == 0) {
			key = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, keyAttachement);
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

	public boolean hasEvent() {
		return !events.isEmpty();
	}

	public Event pollEvent() {
		return events.poll();
	}

	public void close() throws IOException {
		socketChannel.close();
	}

	private void read() throws IOException {
		int nbBytesRead = socketChannel.read(readBuffer);

		if (nbBytesRead == -1) {
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
			socketChannel.write(writeBuffer);

			if (writeBuffer.hasRemaining()) {
				return;
			}
		}

		if (commandWriting != null) {
			events.add(Event.newSent(commandWriting));
		}

		if (commandsToSend.isEmpty()) {
			key = socketChannel.register(selector, SelectionKey.OP_READ, keyAttachement);
			writeBuffer = null;
			commandWriting = null;
		} else {
			Command c = commandsToSend.poll();
			writeBuffer = ByteBuffer.wrap(c.toString().getBytes());
			commandWriting = c;
		}
	}
}
