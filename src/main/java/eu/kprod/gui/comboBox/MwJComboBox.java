package eu.kprod.gui.comboBox;

import javax.swing.JComboBox;



public class MwJComboBox<E> extends JComboBox<E>{

    public MwJComboBox(String name, E[] array) {
        // TODO Auto-generated constructor stub
        super(array);
        super.setRenderer(new MwComboBoxRenderer(name));
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
