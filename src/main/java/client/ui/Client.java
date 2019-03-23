package client.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Client implements Runnable {

	private Screen screen;
	private BasicWindow window;
	private MultiWindowTextGUI gui;
	private TextBox messagesTextBox;
	private MyTextBox messageTextBox;
	private Listener listener;
	private List<Runnable> toInvokeLater;

	public interface Listener {
		void onClosed();

		void onMessageSent(String message);
	}

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

		messageTextBox = new MyTextBox();
		messageTextBox.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)).addTo(panel);
		messageTextBox.setListener(new MyTextBox.Listener() {

			@Override
			public void onClosed() {
				if (listener != null) {
					listener.onClosed();
				}
			}

			@Override
			public void onMessageSent(String message) {
				if (listener != null) {
					listener.onMessageSent(message);
				}
			}

		});

		// Create window to hold the panel
		window = new BasicWindow("l-clavadator");
		window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));
		window.setComponent(panel);

		messageTextBox.takeFocus();

		toInvokeLater = Collections.synchronizedList(new ArrayList<Runnable>());
	}

	@Override
	public void run() {
		// Create gui and start gui
		gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

		for (Runnable runnable : toInvokeLater) {
			gui.getGUIThread().invokeLater(runnable);
		}

		toInvokeLater = null;

		gui.addWindowAndWait(window);

		try {
			screen.stopScreen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (listener != null) {
			listener.onClosed();
		}
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void invokeLater(Runnable runnable) {
		if (gui == null) {
			toInvokeLater.add(runnable);
		} else {
			gui.getGUIThread().invokeLater(runnable);
		}
	}

	public void addMessage(String message) {
		if (messagesTextBox.getText().isEmpty()) {
			messagesTextBox.setText(message);
		} else {
			messagesTextBox.addLine(message);
		}

		for (int i = 0; i < messagesTextBox.getLineCount(); ++i) {
			messagesTextBox.handleKeyStroke(new KeyStroke(KeyType.ArrowDown));
		}
	}

	public void close() {
		if (!window.isVisible()) {
			return;
		}

		window.close();

		if (listener != null) {
			listener.onClosed();
		}
	}

	public void showError(String error) {
		new MessageDialogBuilder().setTitle("Error occured").setText(error).addButton(MessageDialogButton.OK).build()
				.showDialog(gui);
	}
}
