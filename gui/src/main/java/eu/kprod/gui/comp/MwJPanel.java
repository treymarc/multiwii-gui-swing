package eu.kprod.gui.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class MwJPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwJPanel() {
        super();

    }

    public MwJPanel(BorderLayout borderLayout) {
        // TODO Auto-generated constructor stub
        super(borderLayout);
    }

    public MwJPanel(Color background) {
        // TODO Auto-generated constructor stub
        super();
        setBackground(background);
    }

    public MwJPanel(GridLayout gridLayout) {
        super(gridLayout);
    }

}