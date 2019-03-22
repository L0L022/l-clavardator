package client.ui;

import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class MyTextBox extends TextBox {
	private Listener listener;

	public interface Listener {
		void onClosed();

		void onMessageSent(String message);
	}

	@Override
	public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
		if (keyStroke.getKeyType() == KeyType.EOF || (keyStroke.isCtrlDown()
				&& (keyStroke.getCharacter().charValue() == 'd' || keyStroke.getCharacter().charValue() == 'c'))) {

			if (listener != null) {
				listener.onClosed();
			}

			return Result.HANDLED;
		}

		if (keyStroke.getKeyType() == KeyType.Enter) {

			if (listener != null) {
				listener.onMessageSent(getText());
			}

			setText("");

			return Result.HANDLED;
		}

		return super.handleKeyStroke(keyStroke);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
}
