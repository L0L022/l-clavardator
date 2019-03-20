import java.io.IOException;

public class ServerApp {
	public static void main(String[] args) {
		try {
			(new Server()).demarrer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
