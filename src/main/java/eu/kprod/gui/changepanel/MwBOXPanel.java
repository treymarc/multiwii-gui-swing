package eu.kprod.gui.changepanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import eu.kprod.MwDataModel;
import eu.kprod.gui.MwJCheckBox;
import eu.kprod.gui.MwJLabel;


public class MwBOXPanel extends MwChangeablePanel {

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
           this.add(this.build(m.getBOXs(),m.getBoxNameIndex()));
           this.revalidate();
       }
    }

    private Component build(Map<String, List<Boolean>> map, Map<Integer, String> index) {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new GridLayout(1+(index == null ? 0 : index.size()),1));
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(1,5));
//        pane.setBorder(new EmptyBorder(1, 1, 1, 1));
        pane.add(new MwJLabel());
        pane.add(new MwJLabel("aux1"));
        pane.add(new MwJLabel("aux2"));
        pane.add(new MwJLabel("aux3"));
        pane.add(new MwJLabel("aux4"));

        mainPane.add(pane);
        
        if (map == null || index == null){
            return mainPane;
        }
       

        for (int i = 0; i < index.size(); i++) {
            String name = index.get(i);
            pane = new JPanel();
            pane.setLayout(new GridLayout(1,5));
          
            pane.add(new JLabel(name));
            
            List<Boolean> BoxItem = map.get(name);
            int j = 0;
            int auxCnt = 0;
            JPanel auxPane = new JPanel();
            auxPane.setLayout(new GridLayout(1, 3));
//            auxPane.setBorder(new EmptyBorder(0,3,0, 3));
            for (Boolean state : BoxItem) {
                
                // TODO get step and bound  from msp
                JCheckBox chck = new MwJCheckBox(name,j,"aux"+(auxCnt+1));
                chck.setSelected(state);
                auxPane.add(chck);
                j++;
                if (j==3){
                    pane.add(auxPane);
                    auxPane = new JPanel();
                    auxPane.setLayout(new GridLayout(1, 3));
//                    auxPane.setBorder(new EmptyBorder(0,3,0, 3));
                    j=0;
                    auxCnt++;
                }
               
            }
            mainPane.add(pane);
        }
        

        return mainPane;
    }

}
