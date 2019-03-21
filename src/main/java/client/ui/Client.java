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

		final TextBox messagesTextBox = new TextBox().setReadOnly(true)
				.setLayoutData(GridLayout.createLayoutData(Alignment.FILL, Alignment.FILL, true, true)).addTo(panel);

		panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

		final TextBox messageTextBox = new MyTextBox(messagesTextBox)
				.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)).addTo(panel);

		// Create window to hold the panel
		BasicWindow window = new BasicWindow("l-clavadator");
		window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));
		window.setComponent(panel);

		messageTextBox.takeFocus();

		// Create gui and start gui
		MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
				new EmptySpace(TextColor.ANSI.BLUE));
		gui.addWindowAndWait(window);
	}

}
