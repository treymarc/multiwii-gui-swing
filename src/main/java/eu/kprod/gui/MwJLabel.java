package eu.kprod.gui;

import java.awt.Color;

import javax.swing.JLabel;

public class MwJLabel extends JLabel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwJLabel(String s, Color color){
        super(s);
        setForeground(color);
        setHorizontalAlignment(CENTER);
    }
    public MwJLabel(String s){
        super(s);
        setHorizontalAlignment(CENTER);
    }

    public MwJLabel() {
        super();
    }
}
