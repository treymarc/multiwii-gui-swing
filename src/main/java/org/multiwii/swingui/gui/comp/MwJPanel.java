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
package org.multiwii.swingui.gui.comp;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.multiwii.swingui.gui.MwConfiguration;

public class MwJPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public MwConfiguration conf;

    public MwJPanel() {
        super();

    }

    public MwJPanel(final BorderLayout borderLayout) {
        super(borderLayout);
    }

    public MwJPanel(final GridLayout gridLayout) {
        super(gridLayout);
    }

    public MwJPanel(MwConfiguration conf1) {
        // TODO Auto-generated constructor stub
        setBackground(conf1.color.getColor(MwColor.BACKGROUND_COLOR));
        conf = conf1;
    }

}