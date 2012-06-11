package eu.kprod.gui;

import javax.swing.JCheckBox;

public class MwJCheckBox extends JCheckBox {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String state = "l";

    
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

        setToolTipText(new StringBuffer().append(string).append(" ").append(state).append(" : ").append(name).toString());
    }

}
