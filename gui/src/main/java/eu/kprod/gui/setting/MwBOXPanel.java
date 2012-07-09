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
package eu.kprod.gui.setting;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import eu.kprod.ds.MwDataModel;
import eu.kprod.gui.MwConfiguration;
import eu.kprod.gui.comp.MwColor;
import eu.kprod.gui.comp.MwJCheckBox;
import eu.kprod.gui.comp.MwJLabel;
import eu.kprod.gui.comp.MwJPanel;

public class MwBOXPanel extends MwChangeablePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int boxGroupCount = 3;

    public MwBOXPanel(MwConfiguration conf, final String name) {
        super(name);
        super.conf = conf;
    }

    private Component build(final Map<String, List<Boolean>> map,
            final Map<Integer, String> index, boolean[] bs) {
        final MwJPanel mainPane = new MwJPanel();
        mainPane.setLayout(new GridLayout(
                1 + (index == null ? 0 : index.size()), 1));
        MwJPanel pane = new MwJPanel();

        mainPane.add(pane);

        if (map == null || index == null) {
            pane.setLayout(new GridLayout(1, 1));
            pane.add(new MwJLabel(Color.RED, "AUX - EMPTY"));
            mainPane.add(pane);
            return mainPane;
        }

        pane.setLayout(new GridLayout(1, 5));
        pane.add(new MwJLabel());
        pane.add(new MwJLabel("aux1"));
        pane.add(new MwJLabel("aux2"));
        pane.add(new MwJLabel("aux3"));
        pane.add(new MwJLabel("aux4"));
        for (int i = 0; i < index.size(); i++) {
            final String name = index.get(i);
            pane = new MwJPanel();
            pane.setLayout(new GridLayout(1, 5));

            if (bs[i]) {
                pane.add(new MwJLabel(
                        conf.color.getColor(MwColor.ACTIVE_COLOR), name));
            } else {
                pane.add(new MwJLabel(name));

            }

            // List<Boolean> BoxItem = ;
            int j = 0;
            int auxCnt = 0;
            MwJPanel auxPane = new MwJPanel();
            auxPane.setLayout(new GridLayout(1, 3));
            for (final Boolean state : map.get(name)) {

                // TODO get step and bound from msp
                final JCheckBox chck = new MwJCheckBox(name, j, "aux"
                        + (auxCnt + 1));
                chck.setSelected(state);
                auxPane.add(chck);
                j++;
                if (j == boxGroupCount) {
                    pane.add(auxPane);
                    auxPane = new MwJPanel();
                    auxPane.setLayout(new GridLayout(1, boxGroupCount));
                    j = 0;
                    auxCnt++;
                }
            }
            mainPane.add(pane);
        }
        return mainPane;
    }

    @Override
    final void newModel(final MwDataModel m) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                removeAll();
                setLayout(new GridLayout(1, 1));
                add(build(m.getBOXs(), m.getBoxNameIndex(), m.getBoxNameState()));
                revalidate();

            }

        });

    }

}
