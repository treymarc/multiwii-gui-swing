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

import java.awt.Color;
import java.util.Hashtable;
import java.util.Map;

public class MwColorDefault extends MwColor {

    public Map<String,Color> COLOR_GRAPH = getGraphColor();
    public Map<String,Color> COLOR = getColor();

    public Color getColorImpl(int colortype, String colorKey) {
        switch (colortype) {
            case COLORSETGRAPH:
                return COLOR_GRAPH.get(colorKey);
            case COLORSET:
                return COLOR.get(colorKey);
            default:
                return COLOR.get(MwColor.FORGROUND_COLOR);
        }

    }

    /**
     * realtime graph color
     * 
     * @return
     */
    private static  Map<String,Color> getGraphColor() {
        final Map<String,Color> m = new  Hashtable<String,Color>();
        m.put("1",Color.BLUE);
        m.put("2",Color.GREEN);
        m.put("2",Color.YELLOW);
        m.put("3",Color.PINK);
        m.put("4",Color.RED);
        m.put("5",Color.CYAN);
        m.put("6",Color.MAGENTA);
        m.put("7",Color.ORANGE);
        m.put("8",Color.BLACK);
        m.put("9",Color.DARK_GRAY);
        m.put("10",new Color(51, 51, 51));
        m.put("11",new Color(250, 100, 100));
        m.put("12",new Color(250, 200, 100));
        m.put("13",new Color(250, 100, 200));
        m.put("14",new Color(250, 200, 200));

        return m;
    }

    /**
     * gui color
     * 
     * @return
     */
    private static Map<String,Color> getColor() {
        final Map<String,Color> m = new Hashtable<String,Color>();

        m.put(MwColor.BACKGROUND_COLOR,new Color(51, 51, 51)); // BACKGROUND_COLOR
        m.put(MwColor.FORGROUND_COLOR,new Color(204, 204, 204)); // FORGROUND_COLOR
        m.put(MwColor.INSTR_SKY_BLUE,new Color(10, 112, 156)); // INSTR_SKY_BLUE
        m.put(MwColor.INSTR_BAR_GREEN,new Color(96, 220, 113)); // INSTR_BAR_GREEN
        m.put(MwColor.INSTR_EARTH_ORANGE,new Color(202, 112, 14)); // INSTR_EARTH_ORANGE
        m.put(MwColor.INSTR_BAR_RED,new Color(220, 113, 113)); // INSTR_BAR_RED
        m.put(MwColor.INSTR_BAR_YELLOW,new Color(220, 220, 113)); // INSTR_BAR_YELLOW
        m.put(MwColor.ACTIVE_COLOR,new Color(70, 180, 70)); // ACTIVE_COLOR = 0;
        return m;
    }


    void setColorGraphImpl(String index, Color color) {
    	COLOR_GRAPH.remove(index);
        COLOR_GRAPH.put(index, color);
    }



}
