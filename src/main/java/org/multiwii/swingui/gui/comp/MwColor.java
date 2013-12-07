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

public abstract class MwColor {

    public static final int COLORSET = 0;
    // begin
    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final String FORGROUND_COLOR ="FORGROUND_COLOR";
    public static final String INSTR_SKY_BLUE = "INSTR_SKY_BLUE";
    public static final String INSTR_BAR_GREEN = "INSTR_SKY_BLUE";
    public static final String INSTR_EARTH_ORANGE = "INSTR_EARTH_ORANGE";
    public static final String INSTR_BAR_RED = "INSTR_BAR_RED";
    public static final String INSTR_BAR_YELLOW = "INSTR_BAR_YELLOW";
    public static final String ACTIVE_COLOR = "ACTIVE_COLOR";
    // end

    public static final int COLORSETGRAPH = 1;

    // begin
    // we dont need the name
    // end

    /**
     * get the color of timeserie
     * 
     * @param sensorName
     *            of the timeserie
     * @return
     */
    public Color getColorGraph(String sensorName) {
    	Color c = getColorImpl(COLORSETGRAPH, sensorName);
    	if (c == null){
    		c = getColorImpl(COLORSET, FORGROUND_COLOR);
    	}
        return c;
    }

    /**
     * get the color of element
     * 
     * @param key
     *            of the element
     * @return
     */
    public Color getColor(String key) {
        return getColorImpl(COLORSET, key);
    }

    /**
     * change graph color for a timeserie
     * 
     * @param key
     *            of the timeserie
     * @param the
     *            new color
     */
    public void setColorGraph(String key, Color color) {
        setColorGraphImpl(key, color);
    }

    abstract Color getColorImpl(int set, String key);

    abstract void setColorGraphImpl(String index, Color color);

}
