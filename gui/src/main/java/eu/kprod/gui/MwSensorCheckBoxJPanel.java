package eu.kprod.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.kprod.gui.comp.MwJButtonColorChooser;
import eu.kprod.gui.comp.MwJCheckBox;
import eu.kprod.gui.comp.MwJLabel;
import eu.kprod.gui.comp.MwJPanel;
import eu.kprod.gui.comp.StyleColor;

public class MwSensorCheckBoxJPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Map<String, MwJButtonColorChooser> boxs = new HashMap<String, MwJButtonColorChooser>();
    private final Map<Integer, String> boxsIndex = new HashMap<Integer, String>();

    public MwSensorCheckBoxJPanel() {
        super(StyleColor.backGround);
    }

    public MwSensorCheckBoxJPanel(Color c) {
        super(c);
    }

    public void addSensorBox(String sensorName) {
        // TODO Auto-generated method stub
        final MwJButtonColorChooser p = boxs.get(sensorName);
        if (p != null) {
            return;
        } else {
            final int index = boxs.size();

            final MwJPanel pane = new MwJPanel(StyleColor.backGround);
            pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
            final MwJCheckBox c = new MwJCheckBox(sensorName, -1, "sensors");
            c.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent evt) {

                    MwGuiFrame.getChartPanel()
                    .setVisible(index, c.isSelected());

                }
            });

            c.setSelected(true);

            pane.add(c);
            final MwJButtonColorChooser check = new MwJButtonColorChooser(index,
                    sensorName, StyleColor.getColor(index));
            boxs.put(sensorName, check);
            boxsIndex.put(index, sensorName);
            pane.add(check);

            pane.add(new MwJLabel(StyleColor.forGround, sensorName));

            this.setLayout(new GridLayout(boxs.size(), 1));
            this.add(pane);
            this.revalidate();
        }
    }

    public void refreshBox(int index, Color c) {
        boxs.get(boxsIndex.get(index)).setColor(c);
    }

}
