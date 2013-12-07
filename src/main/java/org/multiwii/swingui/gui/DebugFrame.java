/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.multiwii.swingui.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import org.multiwii.swingui.gui.comp.MwJButton;
import org.multiwii.swingui.gui.comp.MwJComboBox;
import org.multiwii.swingui.gui.comp.MwJPanel;
import org.multiwii.swingui.serial.SerialException;
import org.multiwii.swingui.serial.SerialListener;

public class DebugFrame extends JFrame implements SerialListener {

	static class RollingDocument extends PlainDocument {
		/**
		 * max length of the fifo document
		 */
		private static int maxTextLength = 1000;
		/**
         *
         */
		private static final long serialVersionUID = 1L;
		private final JTextArea field;

		public RollingDocument(JTextArea textArea) {
			field = textArea;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {

			if (str == null) {
				return;
			}
			if (field.getText().length() > maxTextLength) {
				super.remove(0, str.length());
			}
			super.insertString(offs, str, a);
		}
	}

	private static final Logger LOGGER = Logger.getLogger(DebugFrame.class);

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private final JCheckBox autoscrollBox;
	private final MwJComboBox lineEndings;
	private final JScrollPane scrollPane;
	private final JButton sendButton;
	private final JTextArea textArea;
	private final JTextField textField;

	public DebugFrame(String title, final MwGuiFrame frame) {
		super(title);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.trace("windowClosing "
						+ e.getSource().getClass().getName() + "\n");
				frame.closeDebugFrame();
			}
		});

		getContentPane().setLayout(new BorderLayout());

		textArea = new JTextArea(16, 40);
		textArea.setDocument(new RollingDocument(textArea));

		textArea.setEditable(false);

		// don't automatically update the caret. that way we can manually decide
		// whether or not to do so based on the autoscroll checkbox.
		((DefaultCaret) textArea.getCaret())
				.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		autoscrollBox = new JCheckBox(("Autoscroll"), true);

		lineEndings = new MwJComboBox("line Ending", new String[] {
				("No line ending"), ("Newline"), ("Carriage return"),
				("Both NL & CR") });
		lineEndings.setSelectedIndex(0);
		lineEndings.setMaximumSize(lineEndings.getMinimumSize());

		scrollPane = new JScrollPane(textArea);

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		MwJPanel pane = new MwJPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.setBorder(new EmptyBorder(1, 1, 1, 1));

		textField = new JTextField(40);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOGGER.trace("actionPerformed "
						+ e.getSource().getClass().getName() + "\n");

				try {
					frame.getCom().send(textField.getText(),
							lineEndings.getSelectedIndex());
				} catch (SerialException e1) {
					LOGGER.error(e1.getMessage() + "\n");
				}
				textField.setText("");
			}
		});

		sendButton = new MwJButton("Send", "Send serial commande");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOGGER.trace("actionPerformed "
						+ e.getSource().getClass().getName() + "\n");

				try {
					frame.getCom().send(textField.getText(),
							lineEndings.getSelectedIndex());
				} catch (SerialException e1) {
					LOGGER.error(e1.getMessage() + "\n");
				}
				textField.setText("");
				if (autoscrollBox.isSelected()) {
					textArea.setCaretPosition(textArea.getDocument()
							.getLength());
				}
			}
		});

		pane.add(textField);
		pane.add(Box.createRigidArea(new Dimension(1, 0)));
		pane.add(sendButton);

		getContentPane().add(pane, BorderLayout.NORTH);

		pane = new MwJPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.setBorder(new EmptyBorder(1, 1, 1, 1));

		pane.add(autoscrollBox);
		pane.add(Box.createHorizontalGlue());
		pane.add(lineEndings);

		getContentPane().add(pane, BorderLayout.SOUTH);

		pack();
		setSize(new Dimension(500, 200));
	}

	/**
	 * add to textArea
	 */
	@Override
	public final void readSerialByte(final int aByte) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textArea.append(String.valueOf((char) aByte));
				if (autoscrollBox.isSelected()) {
					textArea.setCaretPosition(textArea.getDocument()
							.getLength());
				}
			}
		});

	}

	@Override
	public void reportSerial(Throwable e) {
		// TODO Auto-generated method stub
	}
}
