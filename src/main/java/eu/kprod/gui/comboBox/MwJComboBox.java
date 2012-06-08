package eu.kprod.gui.comboBox;

import javax.swing.JComboBox;

public class MwJComboBox extends JComboBox {

    public MwJComboBox(String name, Object[] array) {
        // TODO Auto-generated constructor stub
        super(array);
        setToolTipText(name);
        super.setRenderer(new MwComboBoxRenderer(name));
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
