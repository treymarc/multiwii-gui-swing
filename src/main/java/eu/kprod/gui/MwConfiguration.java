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
package eu.kprod.gui;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import eu.kprod.gui.comp.MwColor;
import eu.kprod.gui.comp.MwColorDefault;

/**
 * Everything for the gui configuration
 *      - resources path
 *              - font
 *              - theme
 *      - colors
 * @author treym
 *
 */
public class MwConfiguration {

    public static final int FONT = 0;
    public static final int THEME = 1;
    

    public MwColor color = new MwColorDefault();
    
    private MwResources pathManager;

    public String getPath(int path) {

        return getPathManager().get(path);
 
    }

    private  MwResources getPathManager() {
        if (pathManager == null){
            pathManager = new MwResources();
        }
        return pathManager;
    }

    public static void setLookAndFeel() {
        // TODO remember OS
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");

        } else {
            try {
                for (final LookAndFeelInfo info : UIManager
                        .getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

            } catch (final Exception e) {
                // continue
            }
        }

    }

    public void setColorGraph(int index, Color colorValue) {
        // TODO Auto-generated method stub
        color.setColorGraph( index,  colorValue);
    }

   
  

}
