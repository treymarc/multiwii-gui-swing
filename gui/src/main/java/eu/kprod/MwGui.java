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
package eu.kprod;

import javax.swing.SwingUtilities;

import eu.kprod.gui.MwConfiguration;
import eu.kprod.gui.MwGuiFrame;


/**
 * Known issues
 * 
 * - when zooming the chart : news values are still recorded so due to the
 * dataSource maxItemcounts and AgeLimite , the chart gets emptied at the zoomed
 * date
 * 
 * @author treym
 * 
 */
public final class MwGui {

    /**
     * @param args
     * @throws SerialException
     */
    public static void main(final String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                MwColorDefault.setLookAndFeel();
//                 MwConfiguration conf = new ;
                 
                 MwGuiFrame.setConf(new MwConfiguration());
                MwGuiFrame.getInstance().setVisible(true);
                MwGuiFrame.getInstance().repaint();

            }

        });

    }

    private MwGui() {

    }

}
