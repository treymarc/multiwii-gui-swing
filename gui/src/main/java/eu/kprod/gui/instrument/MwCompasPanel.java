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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import eu.kprod.ds.MwSensorClass;
import eu.kprod.gui.MwConfiguration;
import eu.kprod.gui.comp.MwColor;
import eu.kprod.msp.MSP;

public class MwCompasPanel extends MwInstrumentJPanel {

    private Image imageCompas= super.getImage("compas.png");
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Double alt = 0.0;
    private Integer head = 0;

   
    public MwCompasPanel(MwConfiguration conf) {
        super(null,conf);
        setBackground(conf.color.getColor(MwColor.BACKGROUND_COLOR));

    }


    private void drawBackground(final Graphics2D g2d) {

        final Float background = new Ellipse2D.Float(
                (getMaxRadiusX() - getRadiusx() * 2) / 2,
                (getMaxRadiusY() - getRadiusy() * 2) / 2, 2 * getRadiusx(),
                2 * getRadiusy());

        g2d.setPaint(Color.DARK_GRAY);
        g2d.fill(background);

    }

    private void drawCompas(final Graphics2D g2d) {
        // rotate to heading
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(-head), getCenterPoint().getX(),
                getCenterPoint().getY());
        g2d.transform(at);

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(conf.color.getColor(MwColor.FORGROUND_COLOR));

        final GeneralPath bankMarker = new GeneralPath(Path2D.WIND_EVEN_ODD);
        bankMarker.moveTo((getCenterPoint().getX() - getRadiusx()),
                getCenterPoint().getY());
        bankMarker.lineTo((getCenterPoint().getX() - getRadiusx() + 6),
                getCenterPoint().getY());

        final AffineTransform ata = AffineTransform.getRotateInstance(Math
                .toRadians(22.5), getCenterPoint().getX(), getCenterPoint()
                .getY());

        for (int i = 0; i < 16; i++) {

            // GeneralPath path;
            GeneralPath path;
            switch (i) {
                case 4:
                    // N
                    path = new GeneralPath(Path2D.WIND_EVEN_ODD);
                    path.moveTo((getCenterPoint().getX() - getRadiusx() + 4),
                            getCenterPoint().getY() - 6);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 19),
                            getCenterPoint().getY() - 6);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 4),
                            getCenterPoint().getY() + 6);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 20),
                            getCenterPoint().getY() + 6);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(path);
                    break;
                case 8:
                    // E
                    path = new GeneralPath(Path2D.WIND_EVEN_ODD);
                    path.moveTo((getCenterPoint().getX() - getRadiusx() + 6),
                            getCenterPoint().getY() + 7);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 16),
                            getCenterPoint().getY() + 7);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 16),
                            getCenterPoint().getY());
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 10),
                            getCenterPoint().getY());
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 16),
                            getCenterPoint().getY());

                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 16),
                            getCenterPoint().getY() - 7);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 6),
                            getCenterPoint().getY() - 7);

                    g2d.setStroke(new BasicStroke(2));
                    break;
                case 12:
                    // S
                    path = new GeneralPath(Path2D.WIND_EVEN_ODD);
                    path.moveTo((getCenterPoint().getX() - getRadiusx() + 3),
                            getCenterPoint().getY() - 5);
                    path.curveTo((getCenterPoint().getX() - getRadiusx() + 12),
                            getCenterPoint().getY() + 25, (getCenterPoint()
                                    .getX() - getRadiusx() + 12),
                                    getCenterPoint().getY() - 25, (getCenterPoint()
                                            .getX() - getRadiusx() + 20),
                                            getCenterPoint().getY() + 5);

                    g2d.setStroke(new BasicStroke(2));
                    break;

                case 0:
                    // W
                    path = new GeneralPath(Path2D.WIND_EVEN_ODD);
                    path.moveTo((getCenterPoint().getX() - getRadiusx() + 4),
                            getCenterPoint().getY() - 6);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 8),
                            getCenterPoint().getY() + 6);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 12),
                            getCenterPoint().getY() - 4);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 16),
                            getCenterPoint().getY() + 6);
                    path.lineTo((getCenterPoint().getX() - getRadiusx() + 20),
                            getCenterPoint().getY() - 6);

                    g2d.setStroke(new BasicStroke(2));
                    break;
                default:
                    g2d.setStroke(new BasicStroke(1));
                    path = bankMarker;
                    break;
            }

            g2d.draw(path);
            g2d.transform(ata);

        }

        at = AffineTransform.getRotateInstance(Math.toRadians(head),
                getCenterPoint().getX(), getCenterPoint().getY());
        g2d.transform(at);

    }

    private void drawUAV(final Graphics2D g2d) {

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(Color.lightGray);

        // triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        // triangle.moveTo(centerPoint.getX(), (centerPoint.getY() - radius +
        // 5));
        // triangle.lineTo((centerPoint.getX() - 15),
        // (centerPoint.getY() - radius + 30));
        // triangle.lineTo((centerPoint.getX() + 15),
        // (centerPoint.getY() - radius + 30));
        // triangle.closePath();
        //
        // g2d.fill(triangle);

        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(conf.color.getColor(MwColor.FORGROUND_COLOR));

        final String k = head.toString();

        g2d.drawString(k, (float) (getCenterPoint().getX() - k.length() * 3),
                (float) getCenterPoint().getY());

        final int w = 200;

        final BufferedImage bi = new BufferedImage(getMaxRadiusY(), getMaxRadiusY(),
                BufferedImage.TYPE_INT_ARGB);
        final Graphics g = bi.getGraphics();
        g.drawImage(imageCompas, 0, 0, null);

        final float[] scales = { 1.0f, 1.0f, 1.0f, 0.8f };
        final float[] offsets = new float[4];
        final RescaleOp rop = new RescaleOp(scales, offsets, null);

        g2d.drawImage(bi, rop, (getMaxRadiusX() - w) / 2,
                (getMaxRadiusY() - w) / 2);

        // g2d.draw(triangle);

    }

    void drawValue(final Graphics2D g2d) {

        g2d.setFont(getWriting());
        g2d.setPaint(conf.color.getColor(MwColor.FORGROUND_COLOR));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString("Alt " + (alt), 10, 10);

    }

    @Override
    public void paintComponent(final Graphics g) {

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawValue(g2d);
        drawBackground(g2d);
        drawCompas(g2d);

        drawUAV(g2d);

        final Float roundHorizon = new Ellipse2D.Float(
                (getMaxRadiusX() - getRadiusx() * 2) / 2,
                (getMaxRadiusY() - getRadiusy() * 2) / 2, 2 * getRadiusx(),
                2 * getRadiusy());

        g2d.setStroke(new BasicStroke(3));
        g2d.draw(roundHorizon);

    }

    @Override
    public void readNewValue(final Integer string, final int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readNewValue(Class<? extends MwSensorClass> sensorClass, final String name, final Double value) {
        if (MSP.IDHEAD.equals(name)) {
            this.head = value.intValue();
        } else if (MSP.IDALT.equals(name)) {
            this.alt = value;
        }

        repaint();
    }

    @Override
    void resetAllValuesImpl() {
        head = 0;
        alt = 0.0;

    }

}
