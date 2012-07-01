package eu.kprod.gui.comp;

import javax.swing.JComboBox;

public class MwJComboBox extends JComboBox {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwJComboBox(final String name, final Object[] array) {
        // TODO Auto-generated constructor stub
        super(array);
        setToolTipText(name);
        super.setRenderer(new MwComboBoxRenderer(name));
    }

}
