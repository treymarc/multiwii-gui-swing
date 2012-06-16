package eu.kprod.gui;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import eu.kprod.gui.comp.MwJCheckBox;
import eu.kprod.gui.comp.MwJLabel;
import eu.kprod.gui.comp.MwJPanel;



public class MwSensorCheckBoxJPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

   Map<String,Boolean> boxs = new HashMap<String, Boolean>();



    public void addSensorBox(String sensorName) {
        // TODO Auto-generated method stub
        Boolean p = boxs.get(sensorName);
        if (Boolean.TRUE.equals(p)){
            return;
        }else{
            boxs.put(sensorName, true);
            
            MwJPanel pane = new MwJPanel();
//            pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
            pane.setLayout(new GridLayout(1,2));
            final MwJCheckBox c = new MwJCheckBox(sensorName, -1, "sensors");
            c.setSelected(true);
            setLayout(new GridLayout(boxs.size(),1));
            pane.add(c);
            pane.add(new MwJLabel(sensorName));
            
            this.add(pane);
            this.revalidate();
        }
    }

}
