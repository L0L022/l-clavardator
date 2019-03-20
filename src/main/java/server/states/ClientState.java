package server.states;

import java.io.IOException;
import java.util.Set;

import protocol.commands.Command;
import server.Client;

public interface ClientState {
	ClientState process(Client self, Set<Client> clients, Command c) throws IOException;
}
