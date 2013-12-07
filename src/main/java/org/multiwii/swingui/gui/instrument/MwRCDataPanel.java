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

package org.multiwii.swingui.gui.instrument;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.multiwii.swingui.ds.MwSensorClass;
import org.multiwii.swingui.gui.MwConfiguration;
import org.multiwii.msp.MSP;

public class MwRCDataPanel extends MwInstrumentJPanel {

	private static final long serialVersionUID = 1L;

	private final Image background = super.getImage("rcdata.png");
	private final int[] startx = initializePositionX();
	private final int[] starty = initializePositionY();
	private final int rcDatabarWidth = 7;

	private int[] initializePositionY() {
		int[] m = new int[8];
		int starty = 16;
		for (int i = 0; i < m.length; i++) {
			m[i] = starty;
			starty += rcDatabarWidth + 8;
		}
		return m;
	}

	private int[] initializePositionX() {
		int[] m = new int[8];
		for (int i = 0; i < m.length; i++) {
			m[i] = 41;
		}
		return m;
	}

	private final double[] dataRC = new double[8];

	public MwRCDataPanel(MwConfiguration conf) {
		super(new Dimension(200, 150), conf);
		super.setBarMax(118);
		super.setBarWidth(rcDatabarWidth);

		for (int i = 0; i < dataRC.length; i++) {
			dataRC[i] = 0;
		}
	}

	private void drawBackground(Graphics2D g2d) {

		// int w = 200;

		BufferedImage bi = new BufferedImage(getMaxRadiusX(), getMaxRadiusY(),
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = bi.getGraphics();
		g.drawImage(background, 0, 0, null);

		// float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
		// float[] offsets = new float[4];
		// RescaleOp rop = new RescaleOp(scales, offsets, null);

		g2d.drawImage(bi, null, 0, 0);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		drawBackground(g2d);

		drawBar(g2d, 2, dataRC, null, startx, starty, XAXIS);
	}

	@Override
	public void readNewValue(Integer string, int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readNewValue(Class<? extends MwSensorClass> sensorClass,
			String name, Double value) {

		if (MSP.IDRCTHROTTLE.equals(name)) {
			dataRC[0] = value;
		} else if (MSP.IDRCPITCH.equals(name)) {
			dataRC[1] = value;
		} else if (MSP.IDRCROLL.equals(name)) {
			dataRC[2] = value;
		} else if (MSP.IDRCYAW.equals(name)) {
			dataRC[3] = value;
		} else if (MSP.IDRCAUX1.equals(name)) {
			dataRC[4] = value;
		} else if (MSP.IDRCAUX2.equals(name)) {
			dataRC[5] = value;
		} else if (MSP.IDRCAUX3.equals(name)) {
			dataRC[6] = value;
		} else if (MSP.IDRCAUX4.equals(name)) {
			dataRC[7] = value;
		}
		repaint();
	}

	@Override
	void resetAllValuesImpl() {
	}
}
