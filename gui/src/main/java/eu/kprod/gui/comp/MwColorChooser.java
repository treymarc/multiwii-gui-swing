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
package eu.kprod.gui.comp;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.kprod.gui.MwGuiFrame;

public final class MwColorChooser extends MwJFrame implements ChangeListener {
    private static JColorChooser chooser;
    private static int index = -1;
    private static MwColorChooser instance;
    /**
     * 
     */
    private static final long serialVersionUID = 6206134450489506329L;

    static void getInstance(final int index1, final Color c) {
        if (instance == null) {
            instance = new MwColorChooser();
        }
        index = index1;
        chooser.setColor(c);
        instance.setVisible(true);
    }

    private MwColorChooser() {
        super();

        final MwJPanel pane = new MwJPanel();
        pane.setLayout(new BorderLayout());

        chooser = new JColorChooser();
        chooser.getSelectionModel().addChangeListener(this);
        chooser.setPreviewPanel(new JPanel());
        pane.add(chooser);
        getContentPane().add(pane);
        pack();
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        if (index >= 0) {
            StyleColor.setColor(index, chooser.getColor());
            MwGuiFrame.getChartPanel().setVisible(index, true);
            MwGuiFrame.getChartCheckBoxPanel().refreshBox(index,
                    chooser.getColor());
        }

    }
}