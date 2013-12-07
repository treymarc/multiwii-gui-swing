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
	public static final int BACKGROUND_COLOR = 0;
	public static final int FORGROUND_COLOR = 1;
	public static final int INSTR_SKY_BLUE = 2;
	public static final int INSTR_BAR_GREEN = 3;
	public static final int INSTR_EARTH_ORANGE = 4;
	public static final int INSTR_BAR_RED = 5;
	public static final int INSTR_BAR_YELLOW = 6;
	public static final int ACTIVE_COLOR = 7;
	// end

	public static final int COLORSETGRAPH = 1;

	// begin
	// we dont need the name
	// end

	/**
	 * get the color of timeserie
	 * 
	 * @param key
	 *            of the timeserie
	 * @return
	 */
	public Color getColorGraph(int key) {
		return getColorImpl(COLORSETGRAPH, key);
	}

	/**
	 * get the color of element
	 * 
	 * @param key
	 *            of the element
	 * @return
	 */
	public Color getColor(int key) {
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
	public void setColorGraph(int key, Color color) {
		setColorGraphImpl(key, color);
	}

	abstract Color getColorImpl(int set, int key);

	abstract void setColorGraphImpl(int index, Color color);

}
