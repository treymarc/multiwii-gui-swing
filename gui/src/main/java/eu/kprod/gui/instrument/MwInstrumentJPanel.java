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
package eu.kprod.gui.instrument;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.InputStream;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.gui.MwGuiRuntimeException;
import eu.kprod.gui.Ress;
import eu.kprod.gui.comp.MwJPanel;

public abstract class MwInstrumentJPanel extends MwJPanel implements
MwDataSourceListener {

    private static final Float FONTSIZE = 12.0f;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Font writing = null;

    public static Font getWriting() {
        return writing;
    }
    public static void setWriting(Font writing) {
        MwInstrumentJPanel.writing = writing;
    }
    private Point2D centerPoint;
    private int dimMarker10Deg=15;
    private int dimMarker5Deg=7;

    private Dimension dimPanel;

    private int maxRadiusX = 200;
    private int maxRadiusY = 200;

    private int radiusx;

    private int radiusy;

    public MwInstrumentJPanel(Dimension dimension) {
        if (dimension == null) {
            // Instance variables initialization
            dimPanel = new Dimension(this.maxRadiusX, this.maxRadiusY);
        } else {
            dimPanel = dimension;
            maxRadiusX=dimPanel.width;
            maxRadiusY=dimPanel.height;
        }
        radiusx=((Double) (0.45 * this.maxRadiusX)).intValue();
        radiusy=((Double) (0.45 * this.maxRadiusY)).intValue();

        // this.setMinimumSize(dimPanel);
        // Define a center point as a reference
        centerPoint=new Point2D.Float(this.maxRadiusX / 2,
                this.maxRadiusY / 2);

        if (writing == null) {

            final InputStream is = this.getClass().getResourceAsStream(
                    Ress.font);

            try {
                writing=Font.createFont(Font.TRUETYPE_FONT, is);

                writing=writing.deriveFont(FONTSIZE);

            } catch (final Exception e) {
                throw new MwGuiRuntimeException("Fonts creation failed",e);
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
        return maxRadiusX;
    }

    public int getMaxRadiusY() {
        return maxRadiusY;
    }

    @Override
    public Dimension getPreferredSize() {
        return dimPanel;
    }

    public int getRadiusx() {
        return radiusx;
    }

    public int getRadiusy() {
        return radiusy;
    }

    @Override
    public void resetAllValues() {
        resetAllValuesImpl();
        repaint();

    }

    abstract void resetAllValuesImpl();

    public void setCenterPoint(Point2D centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setDimMarker10Deg(int dimMarker10Deg) {
        this.dimMarker10Deg = dimMarker10Deg;
    }

    public void setDimMarker5Deg(int dimMarker5Deg) {
        this.dimMarker5Deg = dimMarker5Deg;
    }

    public void setMaxRadiusX(int maxRadiusX) {
        this.maxRadiusX = maxRadiusX;
    }

    public void setMaxRadiusY(int maxRadiusY) {
        this.maxRadiusY = maxRadiusY;
    }

    public void setRadiusx(int radiusx) {
        this.radiusx = radiusx;
    }

    public void setRadiusy(int radiusy) {
        this.radiusy = radiusy;
    }


}
