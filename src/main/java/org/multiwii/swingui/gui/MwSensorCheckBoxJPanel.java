/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.multiwii.swingui.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.multiwii.swingui.gui.comp.MwColor;
import org.multiwii.swingui.gui.comp.MwJButtonColorChooser;
import org.multiwii.swingui.gui.comp.MwJCheckBox;
import org.multiwii.swingui.gui.comp.MwJLabel;
import org.multiwii.swingui.gui.comp.MwJPanel;

public class MwSensorCheckBoxJPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Map<String, MwJButtonColorChooser> boxs = new HashMap<String, MwJButtonColorChooser>();
    private final Map<String, String> boxsIndex = new HashMap<String, String>();

    public MwSensorCheckBoxJPanel(MwConfiguration conf) {
        super(conf);
        
    }

//    public MwSensorCheckBoxJPanel(final Color c) {
//        super(c);
//    }

    public final void addSensorBox(final String sensorName) {
        final MwJButtonColorChooser p = boxs.get(sensorName);
        if (p != null) {
            return;
        } else {
            

            final MwJPanel pane = new MwJPanel(conf);
            pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
            final MwJCheckBox c = new MwJCheckBox(sensorName, -1, "sensors");
            c.addChangeListener(new ChangeListener() {
               
                public void stateChanged(final ChangeEvent evt) {

                    MwGuiFrame.getChartPanel()
                            .setVisible(sensorName, c.isSelected());

                }
            });

            c.setSelected(true);

            pane.add(c);
            final MwJButtonColorChooser check = new MwJButtonColorChooser(
                     sensorName, MwColor.COLORSETGRAPH,conf.color.getColorGraph(sensorName));
            boxs.put(sensorName, check);
            boxsIndex.put(sensorName, sensorName);
            pane.add(check);

            pane.add(new MwJLabel(conf.color.getColor(MwColor.FORGROUND_COLOR), sensorName));

            this.setLayout(new GridLayout(boxs.size(), 1));
            this.add(pane);
            this.revalidate();
        }
    }

    public void refreshBox(final String index, final Color c) {
        boxs.get(boxsIndex.get(index)).setColor(c);
    }

}
