package eu.kprod.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;


public class MwSensorCheckBoxJPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

   Map<String,Boolean> boxs = new HashMap<String, Boolean>();
   
   public MwSensorCheckBoxJPanel() {
    // TODO Auto-generated constructor stub
//       this.setLayout(new FlowLayout());
       this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
}
    public void addSensorBox(String sensorName) {
        // TODO Auto-generated method stub
        Boolean p = boxs.get(sensorName);
        if (Boolean.TRUE.equals(p)){
            return;
        }else{
            boxs.put(sensorName, true);
            
            JPanel pane = new JPanel();
//            pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
            final MwJCheckBox c = new MwJCheckBox(sensorName, -1, "sensors");
            c.setSelected(true);
            pane.add(c);
            pane.add(new MwJLabel(sensorName));
            this.add(pane);
            this.revalidate();
        }
    }

}
