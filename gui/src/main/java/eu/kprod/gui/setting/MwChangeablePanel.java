package eu.kprod.gui.setting;


import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.kprod.ds.MwDataModel;
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

   
    
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
        final Object source = e.getSource();
        if (source instanceof MwDataModel) {
            final MwDataModel m = (MwDataModel) source;
            
            newModel(m);

        }else if (source instanceof MwDataModel) {
                
            // save to file , etc ...
        }
    }



    abstract void newModel(MwDataModel m);
    

}
