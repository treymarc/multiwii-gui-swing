package eu.kprod.gui;

import java.awt.Dimension;

import javax.swing.JCheckBox;

public class MwJCheckBox extends JCheckBox {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String state = "l";

    private static Dimension dim= new Dimension(3,3);
    
    public MwJCheckBox(String name, int j, String string) {
        super();
        // TODO Auto-generated constructor stub
        switch (j) {
            case 0:
                state = "low";
                break;
            case 1:
                state = "middle";
                break;
            case 2:
                state = "high";
                break;
            default:
                break;
        }
//        setSize(dim);
        setPreferredSize(dim);
        setToolTipText(new StringBuffer().append(string).append(" ").append(state).append(" : ").append(name).toString());
    }

}
