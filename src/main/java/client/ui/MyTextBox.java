package client.ui;

import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class MyTextBox extends TextBox {

	private TextBox messages;

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
			if (messages.getText().isEmpty()) {
				messages.setText(getText());
			} else {
				messages.addLine(getText());
			}

			setText("");

			for (int i = 0; i < messages.getLineCount(); ++i) {
				messages.handleKeyStroke(new KeyStroke(KeyType.ArrowDown));
			}

			return Result.HANDLED;
		}

		return super.handleKeyStroke(keyStroke);
	}
}
