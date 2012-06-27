package eu.kprod.gui.comp;

import java.awt.Color;

import javax.swing.JLabel;

public class MwJLabel extends JLabel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwJLabel(Color color, String s) {
        super(s);
        setForeground(color);
        setHorizontalAlignment(CENTER);
    }

    public MwJLabel(String s) {
        super(s);
        setHorizontalAlignment(CENTER);
    }

    public MwJLabel() {
        super();
    }

  
}
