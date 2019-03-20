import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class Stream {
	SocketChannel sc;
	String readLine;
	ByteBuffer readBuffer;
	ByteBuffer writeBuffer;
	Queue<Command> receivedCommands;
	Queue<Command> commandsToSend;

	Stream(SocketChannel sc) {
		this.sc = sc;
		readLine = "";
		readBuffer = ByteBuffer.allocate(128);
		receivedCommands = new ArrayDeque<Command>();
		commandsToSend = new ArrayDeque<Command>();
	}

	void send(Command c) {
		commandsToSend.add(c);
	}

	void work(int ops) throws IOException {
		if ((ops & SelectionKey.OP_READ) != 0) {
			read();
		}

		if ((ops & SelectionKey.OP_WRITE) != 0) {
			write();
		}
	}

	void read() throws IOException {
		sc.read(readBuffer);
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

	void write() throws IOException {
		if (writeBuffer != null) {
			sc.write(writeBuffer);

			if (!writeBuffer.hasRemaining()) {
				writeBuffer = null;
			}
		}

		if (writeBuffer == null && !commandsToSend.isEmpty()) {
			Command c = commandsToSend.poll();
			writeBuffer = ByteBuffer.wrap(c.toString().getBytes()); // utiliser le buff prec
		}
	}
}
