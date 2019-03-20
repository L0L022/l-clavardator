package protocol.commands;

public class EndOfStream extends Command {
	@Override
	public String toString() {
		return "EOF\n";
	}
}
