import java.io.IOException;

import client.Client;

public class ClientApp {
	public static void main(String[] args) {
		try {
			(new Client()).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
