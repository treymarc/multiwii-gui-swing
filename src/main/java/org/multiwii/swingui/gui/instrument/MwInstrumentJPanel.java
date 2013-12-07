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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.net.URL;

import org.multiwii.swingui.ds.MwDataSourceListener;
import org.multiwii.swingui.gui.MwConfiguration;
import org.multiwii.swingui.gui.MwGuiRuntimeException;
import org.multiwii.swingui.gui.comp.MwColor;
import org.multiwii.swingui.gui.comp.MwJPanel;

public abstract class MwInstrumentJPanel extends MwJPanel implements
		MwDataSourceListener {

	private static final Float FONTSIZE = 12.0f;

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	static final int XAXIS = 0;
	static final int YAXIS = 1;

	private static Font writing = null;

	public static Font getWriting() {
		return writing;
	}

	public static void setWriting(Font writing1) {
		MwInstrumentJPanel.writing = writing1;
	}

	private Point2D centerPoint;
	private int dimMarker10Deg = 15;
	private int dimMarker5Deg = 7;

	private Dimension dimPanel;

	private int maxSizeX = 200;
	private int maxSizeY = 200;

	private int sizeX;
	private int sizey;

	public MwInstrumentJPanel(Dimension dimension, MwConfiguration conf) {
		this.conf = conf;
		setBackground(conf.color.getColor(MwColor.BACKGROUND_COLOR));
		if (dimension == null) {
			// Instance variables initialization
			dimPanel = new Dimension(this.maxSizeX, this.maxSizeY);
		} else {
			dimPanel = dimension;
			maxSizeX = dimPanel.width;
			maxSizeY = dimPanel.height;
		}
		sizeX = ((Double) (0.45 * this.maxSizeX)).intValue();
		sizey = ((Double) (0.45 * this.maxSizeY)).intValue();

		// this.setMinimumSize(dimPanel);
		// Define a center point as a reference
		centerPoint = new Point2D.Float(this.maxSizeX / 2, this.maxSizeY / 2);

		if (writing == null) {

			InputStream is = this.getClass().getResourceAsStream(
					conf.getPath(MwConfiguration.FONT));

			try {
				writing = Font.createFont(Font.TRUETYPE_FONT, is);
				writing = writing.deriveFont(FONTSIZE);

			} catch (Exception e) {
				throw new MwGuiRuntimeException("Fonts creation failed", e);
			}
		}
	}

	public Point2D getCenterPoint() {
		return centerPoint;
	}

	public int getDimMarker10Deg() {
		return dimMarker10Deg;
	}

	public int getDimMarker5Deg() {
		return dimMarker5Deg;
	}

	public int getMaxRadiusX() {
		return maxSizeX;
	}

	public int getMaxRadiusY() {
		return maxSizeY;
	}

	@Override
	public Dimension getPreferredSize() {
		return dimPanel;
	}

	public int getRadiusx() {
		return sizeX;
	}

	public int getRadiusy() {
		return sizey;
	}

	@Override
	public void resetAllValues() {
		resetAllValuesImpl();
		repaint();

	}

	abstract void resetAllValuesImpl();

	public void setCenterPoint(Point2D centerPoint1) {
		this.centerPoint = centerPoint1;
	}

	public void setDimMarker10Deg(int dimMarker10Deg1) {
		this.dimMarker10Deg = dimMarker10Deg1;
	}

	public void setDimMarker5Deg(int dimMarker5Deg1) {
		this.dimMarker5Deg = dimMarker5Deg1;
	}

	public void setMaxRadiusX(int maxRadiusX1) {
		this.maxSizeX = maxRadiusX1;
	}

	public void setMaxRadiusY(int maxRadiusY1) {
		this.maxSizeY = maxRadiusY1;
	}

	public void setRadiusx(int radiusx1) {
		this.sizeX = radiusx1;
	}

	public void setRadiusy(int radiusy1) {
		this.sizey = radiusy1;
	}

	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	public int getBarMax() {
		return barMax;
	}

	public void setBarMax(int barMax) {
		this.barMax = barMax;
	}

	// bar w/h
	private int barWidth = 8;
	private int barMax = 67;

	MwConfiguration conf;

	/**
	 * will draw bar value for each position defined in xpoint,ypoint ; there
	 * must enough value to draw in values
	 * 
	 * @param g2d
	 *            is the graphic (where we draw)
	 * @param offset
	 *            the bar limit , allow the bar to reach barMax + offset and
	 *            -offest value
	 * @param values
	 *            , All the values to draw
	 * @param indexes
	 *            of the value to draw , use null to draw all value defined by
	 *            xpoint,ypoint
	 * @param xpoint
	 *            , the x position for each bar to draw
	 * @param ypoint
	 *            , the y position for each bar to draw
	 * @param orientation
	 *            , can be YAXIS or XAXIS
	 */
	protected void drawBar(Graphics2D g2d, int offset, double[] values,
			int[] indexes, int[] xpoint, int[] ypoint, int orientation) {

		GeneralPath bar = new GeneralPath(Path2D.WIND_EVEN_ODD);
		int barValue;
		int barMaxNumber;

		if (indexes == null) {
			barMaxNumber = xpoint.length;
		} else {
			barMaxNumber = indexes.length;
		}

		for (int i = 0; i < barMaxNumber; i++) {

			if (indexes == null) {
				barValue = new Double(((values[i] - 1000) / 1000) * barMax)
						.intValue();
			} else {
				barValue = new Double(((values[indexes[i]] - 1000) / 1000)
						* barMax).intValue();
			}

			if (barValue < -offset) {
				barValue = -offset;
			} else if (barValue > barMax + offset) {
				barValue = barMax + offset;
			}

			if (barValue < 0) {
				g2d.setPaint(conf.color.getColor(MwColor.INSTR_BAR_YELLOW));
			} else if (barValue > barMax) {
				g2d.setPaint(conf.color.getColor(MwColor.INSTR_BAR_RED));
			} else {
				g2d.setPaint(conf.color.getColor(MwColor.INSTR_BAR_GREEN));
			}
			g2d.setStroke(new BasicStroke(1));
			bar = new GeneralPath(Path2D.WIND_EVEN_ODD);
			bar.moveTo(xpoint[i], ypoint[i]);

			switch (orientation) {
			case YAXIS:
				bar.lineTo(xpoint[i], ypoint[i] - barValue);
				bar.lineTo(xpoint[i] + barWidth, ypoint[i] - barValue);
				bar.lineTo(xpoint[i] + barWidth, ypoint[i]);
				break;

			case XAXIS:
				bar.lineTo(xpoint[i] + barValue, ypoint[i]);
				bar.lineTo(xpoint[i] + barValue, ypoint[i] + barWidth);
				bar.lineTo(xpoint[i], ypoint[i] + barWidth);

				break;
			default:
				throw new MwGuiRuntimeException("coding bug, please report");
			}

			bar.closePath();
			g2d.fill(bar);

		}
	}

	Image getImage(String image) {
		String fpath = conf.getPath(MwConfiguration.THEME) + image;

		try {
			// System.out.println("fpath:"+fpath);
			URL url = this.getClass().getResource(fpath);
			return Toolkit.getDefaultToolkit().getImage(url);

		} catch (Throwable e) {
			// this never works, images seem to be loaded on another thread
			throw new MwGuiRuntimeException("Could not load image : " + fpath,
					e);
		}
	}
}
