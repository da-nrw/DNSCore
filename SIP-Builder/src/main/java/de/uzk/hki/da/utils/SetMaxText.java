/**
 * 
 */
package de.uzk.hki.da.utils;

import java.awt.Image;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import de.uzk.hki.da.gui.Gui;
import de.uzk.hki.da.gui.GuiMessageWriter;

/**
 * @author gbender
 *
 */
public class SetMaxText extends PlainDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7793450687275516072L;

	private int limit;
	// optional uppercase conversion
	private boolean toUppercase = false;
	
	GuiMessageWriter message = new GuiMessageWriter();

	public SetMaxText(int limit) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) { //throws BadLocationException 
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit) {
			if (toUppercase)
				str = str.toUpperCase();
			try {
				super.insertString(offset, str, attr);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			message.showMessage("Die maximal erlaubte Anzahl von " + limit + " Zeichen ist erreicht." );
		}
	}

}
