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

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JTabbedPane;

import org.multiwii.swingui.gui.comp.MwJPanel;
import org.multiwii.swingui.gui.comp.MwJTabbedPane;

public class MwMainPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwMainPanel(final Component realTimePanel,
            final Component settingsPanel) {
        // take all the place
        super(new GridLayout(1, 1));

        final JTabbedPane tabbedPane = new MwJTabbedPane();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("RealTime Data", null, realTimePanel, null);
        tabbedPane.addTab("Settings", null, settingsPanel, null);

        this.add(tabbedPane);

    }

}