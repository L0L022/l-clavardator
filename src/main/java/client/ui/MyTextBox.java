package client.ui;

import java.io.IOException;

import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import protocol.commands.Message;

public class MyTextBox extends TextBox {

	private TextBox messages;
	public client.network.Client networkClient;

	public MyTextBox(TextBox messages) {
		this.messages = messages;
	}

	@Override
	public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
		if (keyStroke.getKeyType() == KeyType.EOF || (keyStroke.isCtrlDown()
				&& (keyStroke.getCharacter().charValue() == 'd' || keyStroke.getCharacter().charValue() == 'c'))) {
			messages.addLine("FIN");
			return Result.HANDLED;
		}

		if (keyStroke.getKeyType() == KeyType.Enter) {
			try {
				networkClient.send(new Message(getText()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setText("");

			return Result.HANDLED;
		}

		return super.handleKeyStroke(keyStroke);
	}
}
