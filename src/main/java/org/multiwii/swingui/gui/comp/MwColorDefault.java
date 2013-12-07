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
import java.util.ArrayList;
import java.util.List;

public class MwColorDefault extends MwColor {

	public List<Color> COLOR_GRAPH = getGraphColor();
	public List<Color> COLOR = getColor();

	public Color getColorImpl(int colortype, int colorKey) {
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
	private static List<Color> getGraphColor() {
		final List<Color> m = new ArrayList<Color>();
		m.add(Color.BLUE);
		m.add(Color.GREEN);
		m.add(Color.YELLOW);
		m.add(Color.PINK);
		m.add(Color.RED);
		m.add(Color.CYAN);
		m.add(Color.MAGENTA);
		m.add(Color.ORANGE);
		m.add(Color.BLACK);
		m.add(Color.DARK_GRAY);
		m.add(new Color(51, 51, 51));
		m.add(new Color(250, 100, 100));
		m.add(new Color(250, 200, 100));
		m.add(new Color(250, 100, 200));
		m.add(new Color(250, 200, 200));

		return m;
	}

	/**
	 * gui color
	 * 
	 * @return
	 */
	private static List<Color> getColor() {
		final List<Color> m = new ArrayList<Color>();

		m.add(new Color(51, 51, 51)); // BACKGROUND_COLOR
		m.add(new Color(204, 204, 204)); // FORGROUND_COLOR
		m.add(new Color(10, 112, 156)); // INSTR_SKY_BLUE
		m.add(new Color(96, 220, 113)); // INSTR_BAR_GREEN
		m.add(new Color(202, 112, 14)); // INSTR_EARTH_ORANGE
		m.add(new Color(220, 113, 113)); // INSTR_BAR_RED
		m.add(new Color(220, 220, 113)); // INSTR_BAR_YELLOW
		m.add(new Color(70, 180, 70)); // ACTIVE_COLOR = 0;
		return m;
	}

	@Override
	void setColorGraphImpl(int index, Color color) {
		COLOR_GRAPH.set(index, color);
	}

}
