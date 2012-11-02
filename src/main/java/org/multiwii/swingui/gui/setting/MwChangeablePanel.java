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
package org.multiwii.swingui.gui.setting;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.multiwii.swingui.ds.MwDataModel;
import org.multiwii.swingui.gui.comp.MwJLabel;
import org.multiwii.swingui.gui.comp.MwJPanel;

public abstract class MwChangeablePanel extends MwJPanel implements
        ChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwChangeablePanel(final String name) {
        setLayout(new GridLayout(1, 1));
        final Border title = BorderFactory.createTitledBorder((Border) null);
        // title.setTitleJustification(TitledBorder.CENTER);
        add(new MwJLabel(name));
        setBorder(title);
    }

    abstract void newModel(MwDataModel m);

    @Override
    public void stateChanged(final ChangeEvent e) {
        final Object source = e.getSource();
        if (source instanceof MwDataModel) {
            final MwDataModel m = (MwDataModel) source;

            newModel(m);

        }
        // else if (source instanceof JMenu) {
        //
        // // save to file , etc ...
        // }
    }

}
