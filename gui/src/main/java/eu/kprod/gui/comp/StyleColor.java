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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public final class StyleColor {

    public static final Color backGround = new Color(51, 51, 51);

    public static final Color blueSky = new Color(10, 112, 156);
    public static final List<Color> colors = initializeMap();

    public static final Color forGround = new Color(204, 204, 204);
    public static final Color greenBar = new Color(96, 220, 113);
    public static final Color orangeEarth = new Color(202, 112, 14);
    public static final Color redBar = new Color(220, 113, 113);
    public static final Color yellow = new Color(220, 220, 113);

    public static Color getColor(int l) {

        return colors.get(l);
    }

    private static List<Color> initializeMap() {
        final List<Color> m = new ArrayList<Color>();
        m.add(Color.BLUE);
        m.add(Color.GREEN);
        m.add(Color.YELLOW);
        m.add(Color.PINK);
        m.add(Color.RED);
        m.add(Color.CYAN);
        m.add(Color.MAGENTA);
        m.add(Color.ORANGE);
        m.add(StyleColor.forGround);
        m.add(Color.BLACK);
        m.add(Color.DARK_GRAY);

        m.add(new Color(250, 100, 100));
        m.add(new Color(250, 200, 100));
        m.add(new Color(250, 100, 200));
        m.add(new Color(250, 200, 200));

        return m;
    }

    public static void setColor(int index, Color color) {
        colors.set(index, color);

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

    private StyleColor() {

    }
}
