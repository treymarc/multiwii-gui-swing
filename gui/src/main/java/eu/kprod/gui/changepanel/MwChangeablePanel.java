package eu.kprod.gui.changepanel;


import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

import eu.kprod.gui.comp.MwJLabel;
import eu.kprod.gui.comp.MwJPanel;

abstract public class MwChangeablePanel extends MwJPanel implements ChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwChangeablePanel( String name) {
        setLayout(new GridLayout(1,1));
        Border title = BorderFactory.createTitledBorder((Border)null);
//        title.setTitleJustification(TitledBorder.CENTER);
        add(new MwJLabel(name));
        setBorder(title);
    }

   


}
