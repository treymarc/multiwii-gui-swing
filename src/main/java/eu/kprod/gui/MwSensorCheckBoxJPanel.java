package eu.kprod.gui;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.kprod.gui.comp.MwJCheckBox;
import eu.kprod.gui.comp.MwJLabel;
import eu.kprod.gui.comp.MwJPanel;



public class MwSensorCheckBoxJPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

   Map<String,Integer> boxs = new HashMap<String, Integer>();



    public void addSensorBox(String sensorName) {
        // TODO Auto-generated method stub
        Integer p = boxs.get(sensorName);
        if (p != null && p >=0 ){
            return;
        }else{
            final int index =boxs.size();
            boxs.put(sensorName, index);
            
            MwJPanel pane = new MwJPanel();
//            pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
            pane.setLayout(new GridLayout(1,1));
            final MwJCheckBox c = new MwJCheckBox(sensorName, -1, "sensors");
            c.addChangeListener(new ChangeListener () {
                public void stateChanged (ChangeEvent evt) {
                  MwGuiFrame.getRealTimeChart().setVisible(index, c.isSelected());
                 
                }
             });


            c.setSelected(true);
            setLayout(new GridLayout(boxs.size(),1));
            pane.add(c);
            pane.add(new MwJLabel(sensorName));
            
            this.add(pane);
            this.revalidate();
        }
    }

}
