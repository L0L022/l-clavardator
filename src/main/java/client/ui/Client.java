package client.ui;

import java.io.IOException;
import java.util.Collections;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import protocol.Event;
import protocol.commands.EndOfStream;
import protocol.commands.Message;

public class Client implements Runnable {

	private Screen screen;
	private BasicWindow window;
	private MultiWindowTextGUI gui;
	private TextBox messagesTextBox;
	public client.network.Client networkClient;
	private MyTextBox messageTextBox;

	public Client() throws IOException {
		// Setup terminal and screen layers
		Terminal terminal = new DefaultTerminalFactory().createTerminal();
		screen = new TerminalScreen(terminal);
		screen.startScreen();

		// Create panel to hold components
		Panel panel = new Panel();
		panel.setLayoutManager(new GridLayout(1));

		messagesTextBox = new TextBox().setReadOnly(true)
				.setLayoutData(GridLayout.createLayoutData(Alignment.FILL, Alignment.FILL, true, true)).addTo(panel);

		panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

		messageTextBox = new MyTextBox(messagesTextBox);
		messageTextBox.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)).addTo(panel);

		// Create window to hold the panel
		window = new BasicWindow("l-clavadator");
		window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));
		window.setComponent(panel);

		messageTextBox.takeFocus();

	}

	@Override
	public void run() {
		messageTextBox.networkClient = networkClient;

		// Create gui and start gui
		gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
		gui.addWindowAndWait(window);

		try {
			networkClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void process(Event event) {
		if (event.isReceived() && event.command instanceof Message) {
			String message = ((Message) event.command).message;

			if (messagesTextBox.getText().isEmpty()) {
				messagesTextBox.setText(message);
			} else {
				messagesTextBox.addLine(message);
			}

			for (int i = 0; i < messagesTextBox.getLineCount(); ++i) {
				messagesTextBox.handleKeyStroke(new KeyStroke(KeyType.ArrowDown));
			}
		}

		if (event.isReceived() && event.command instanceof EndOfStream) {
			window.close();
		}
	}

}
