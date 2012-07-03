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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import eu.kprod.gui.MwGuiRuntimeException;
import eu.kprod.gui.Ress;
import eu.kprod.gui.comp.StyleColor;
import eu.kprod.msp.MSP;

public class MwRCDataPanel extends MwInstrumentJPanel {

    // private GeneralPath bar;

    private static Image imageRCdataeBg;

    private static int maxoffest = 2;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final double[] dataRC = new double[8];

    {
        if (imageRCdataeBg == null) {

            final URL url = this.getClass().getResource(Ress.IMG_RCDATA);

            try {
                imageRCdataeBg = Toolkit.getDefaultToolkit().getImage(url);

            } catch (final Exception e) {
                throw new MwGuiRuntimeException("Could not load images for "
                        + this.getClass(), e);
            }
        }
    }

    public MwRCDataPanel(final Color c) {

        super(new Dimension(200, 150));
        for (int i = 0; i < dataRC.length; i++) {
            dataRC[i] = 0;
        }
        setBackground(c);

    }

    private void drawBackground(final Graphics2D g2d) {

        // int w = 200;

        final BufferedImage bi = new BufferedImage(getMaxRadiusX(),
                getMaxRadiusY(), BufferedImage.TYPE_INT_ARGB);
        final Graphics g = bi.getGraphics();
        g.drawImage(imageRCdataeBg, 0, 0, null);

        // float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
        // float[] offsets = new float[4];
        // RescaleOp rop = new RescaleOp(scales, offsets, null);

        g2d.drawImage(bi, null, 0, 0);

    }

    private void drawBarValue(final Graphics2D g2d) {
        final int startx = 41;
        int starty = 16;

        // bar w/h
        final int xx = 118;
        final int yy = 7;

        g2d.setStroke(new BasicStroke(1));
        // g2d.setPaint(StyleColor.greenBar);
        for (final double element : dataRC) {

            int barvalue = new Double(((element - 1000) / 1000) * xx)
                    .intValue();
            if (barvalue < -maxoffest) {
                barvalue = -maxoffest;
            } else if (barvalue > xx + maxoffest) {
                barvalue = xx + maxoffest;
            }
            final GeneralPath bar = new GeneralPath(Path2D.WIND_EVEN_ODD);
            bar.moveTo(startx, starty);
            bar.lineTo(startx + barvalue, starty);
            bar.lineTo(startx + barvalue, starty + yy);
            bar.lineTo(startx, starty + yy);
            bar.closePath();
            if (barvalue < 0) {
                g2d.setPaint(StyleColor.INSTR_BAR_YELLOW);
            } else if (barvalue > xx) {
                g2d.setPaint(StyleColor.INSTR_BAR_RED);
            } else {
                g2d.setPaint(StyleColor.INSTR_BAR_GREEN);
            }
            g2d.fill(bar);
            starty += yy + 8;

        }

    }

    @Override
    public void paintComponent(final Graphics g) {

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);

        drawBarValue(g2d);

    }

    @Override
    public void readNewValue(final Integer string, final int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readNewValue(final String name, final Double value) {

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
