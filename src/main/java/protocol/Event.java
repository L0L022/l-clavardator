package protocol;

import protocol.commands.Command;

public class Event {
	enum Type {
		Received, Sent,
	}

	public Type type;
	public Command command;

	public Event(Type type, Command command) {
		this.type = type;
		this.command = command;
	}

	public boolean isReceived() {
		return type == Type.Received;
	}

	public boolean isSent() {
		return type == Type.Sent;
	}

	static Event newReceived(Command command) {
		return new Event(Type.Received, command);
	}

	static Event newSent(Command command) {
		return new Event(Type.Sent, command);
	}
}