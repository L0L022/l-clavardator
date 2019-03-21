package client.ui;

import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Client implements Runnable {

	@Override
	public void run() {
		// Setup terminal and screen layers
		Terminal terminal;
		Screen screen;

		try {
			terminal = new DefaultTerminalFactory().createTerminal();
			screen = new TerminalScreen(terminal);
			screen.startScreen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// Create panel to hold components
		Panel panel = new Panel();
		panel.setLayoutManager(new GridLayout(1));

		panel.addComponent(new Label("l-clavadator"));

		final TextBox messagesTextBox = new TextBox(new TerminalSize(70, 10)).setReadOnly(true).addTo(panel);

		panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

		final TextBox messageTextBox = new MyTextBox(messagesTextBox).setPreferredSize(new TerminalSize(70, 1))
				.addTo(panel);

		panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
		new Button("Send", new Runnable() {
			@Override
			public void run() {
				messagesTextBox.addLine(messageTextBox.getText());
				for (int i = 0; i < messagesTextBox.getLineCount(); ++i) {
					messagesTextBox.handleKeyStroke(new KeyStroke(KeyType.ArrowDown));
				}
			}
		}).addTo(panel);

		panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

		// Create window to hold the panel
		BasicWindow window = new BasicWindow();
		window.setComponent(panel);

		// Create gui and start gui
		MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
				new EmptySpace(TextColor.ANSI.BLUE));
		gui.addWindowAndWait(window);
	}

}
