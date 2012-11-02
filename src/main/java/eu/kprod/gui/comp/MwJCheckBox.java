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

import javax.swing.JCheckBox;

public class MwJCheckBox extends JCheckBox {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String state = "";

    public MwJCheckBox(final String name, final int j, final String string) {
        super();

        // TODO Auto-generated constructor stub
        switch (j) {
            case 0:
                state = "low";
                break;
            case 1:
                state = "middle";
                break;
            case 2:
                state = "high";
                break;
            default:
                break;
        }

        setToolTipText(new StringBuffer().append(string).append(" ")
                .append(state).append(" : ").append(name).toString());
    }

}
