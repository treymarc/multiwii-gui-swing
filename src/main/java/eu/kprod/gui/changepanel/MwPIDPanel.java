package eu.kprod.gui.changepanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;

import eu.kprod.ds.MwDataModel;
import eu.kprod.gui.comp.MwJLabel;
import eu.kprod.gui.comp.MwJPanel;
import eu.kprod.gui.comp.MwTextField;


public class MwPIDPanel extends MwChangeablePanel  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
       Object source = e.getSource();
       if (source instanceof MwDataModel){
           MwDataModel m = (MwDataModel)source;
           this.removeAll();
           super.setLayout(new GridLayout(1,1));
           this.add(this.build(m.getPIDs(),m.getPidNameIndex()));
           this.revalidate();
       }
    }

    private Component build(Map<String, List<Double>> piDs, Map<Integer, String> index) {
        MwJPanel mainPane = new MwJPanel();
        mainPane.setLayout(new GridLayout(1+(index == null ? 0 : index.size()),1));
        MwJPanel pane = new MwJPanel();
       
        if ( piDs == null || index == null ){
            pane.setLayout(new GridLayout(1,1));
            pane.add(new MwJLabel("PID - EMPTY"));
            mainPane.add(pane);
            return mainPane;
        }
        
        pane.setLayout(new GridLayout(1,4));
//        pane.setBorder(new EmptyBorder(1, 1, 1, 1));
        pane.add(new MwJLabel());
        pane.add(new MwJLabel("P"));
        pane.add(new MwJLabel("I"));
        pane.add(new MwJLabel("D"));
        mainPane.add(pane);
        
        for (int i = 0; i < index.size(); i++) {
            String name = index.get(i);
            pane = new MwJPanel();
            pane.setLayout(new GridLayout(1,4));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));
            
            List<Double> pidItem = piDs.get(name);
            pane.add(new JLabel(name));
            int j = 0;
            for (Double double1 : pidItem) {
                
                // TODO get step and bound  from msp
                
                pane.add(new MwTextField(double1,0.1,j++));
            }
            mainPane.add(pane);
        }

        return mainPane;
    }


}
