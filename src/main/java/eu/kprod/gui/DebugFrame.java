package eu.kprod.gui;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import eu.kprod.MwGuiFrame;
import eu.kprod.gui.comboBox.MwJComboBox;
import eu.kprod.serial.SerialException;
import eu.kprod.serial.SerialListener;

public class DebugFrame extends JFrame implements SerialListener {

    static class RollingDocument extends PlainDocument {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * max length of the fifo document
         */
        private static int MAX_LENGTH = 1000;
        private JTextArea field;

        public RollingDocument(JTextArea textArea) {
            field = textArea;
        }

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {

            if (str == null)
                return;

            if (field.getText().length() > MAX_LENGTH) {
                super.remove(0, str.length());
            }
            super.insertString(offs, str, a);
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(DebugFrame.class);

    JTextArea textArea;
    JScrollPane scrollPane;
    final JTextField textField;
    JButton sendButton;
    JCheckBox autoscrollBox;
    final MwJComboBox lineEndings;

    public DebugFrame(final String title  ) {
        super(title);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                logger.trace("windowClosing "
                        + e.getSource().getClass().getName());
                MwGuiFrame.closeDebugFrame();
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

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.setBorder(new EmptyBorder(1, 1, 1, 1));

        textField = new JTextField(40);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.trace("actionPerformed "
                        + e.getSource().getClass().getName());

                try {
                    MwGuiFrame.getCom().send(textField.getText(),
                            lineEndings.getSelectedIndex());
                } catch (SerialException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                textField.setText("");
            }
        });

        sendButton = new MwJButton("Send","Send serial commande");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.trace("actionPerformed "
                        + e.getSource().getClass().getName());

                try {
                    MwGuiFrame.getCom().send(textField.getText(),
                            lineEndings.getSelectedIndex());
                } catch (SerialException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                textField.setText("");
            }
        });

        pane.add(textField);
        pane.add(Box.createRigidArea(new Dimension(1, 0)));
        pane.add(sendButton);

        getContentPane().add(pane, BorderLayout.NORTH);

        pane = new JPanel();
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
    public void readSerialByte(final byte newMessage) {
        textArea.append(String.valueOf((char) newMessage));
        if (autoscrollBox.isSelected()) {
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }

    }

    @Override
    public void reportSerial(Throwable e) {
        // TODO Auto-generated method stub

    }

}
