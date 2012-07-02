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
import eu.kprod.gui.comp.StyleColor;

public class MwUAVPanel extends MwInstrumentJPanel {

    private static Image[] images = new Image[14];
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final double[] motor = new double[8];
    private int uavType = 10;

    // bar w/h
    private final int xx = 8;

    private final int yy = 67;

    {

        try {
            for (int i = 1; i < images.length; i++) {

                final URL urlfw = this.getClass().getResource(
                        "/uav/" + i + ".png");

                images[i] = Toolkit.getDefaultToolkit().getImage(urlfw);
            }

        } catch (final Exception e) {
            throw new MwGuiRuntimeException("Could not load images for "
                    + this.getClass(), e);
        }

    }

    public MwUAVPanel(Color c) {
        super(new Dimension(170, 200));

        setBackground(c);

    }

    /**************** main Mix Table ******************/
    // #ifdef BI
    // motor[0] = PIDMIX(+1, 0, 0); //LEFT
    // motor[1] = PIDMIX(-1, 0, 0); //RIGHT
    // servo[4] = constrain(1500 + YAW_DIRECTION * (axisPID[YAW] +
    // axisPID[PITCH]), 1020, 2000); //LEFT
    // servo[5] = constrain(1500 + YAW_DIRECTION * (axisPID[YAW] -
    // axisPID[PITCH]), 1020, 2000); //RIGHT
    // #endif
    // #ifdef TRI
    // motor[0] = PIDMIX( 0,+4/3, 0); //REAR
    // motor[1] = PIDMIX(-1,-2/3, 0); //RIGHT
    // motor[2] = PIDMIX(+1,-2/3, 0); //LEFT
    // servo[5] = constrain(conf.tri_yaw_middle + YAW_DIRECTION * axisPID[YAW],
    // TRI_YAW_CONSTRAINT_MIN, TRI_YAW_CONSTRAINT_MAX); //REAR
    // #endif
    //
    // #endif
    // #ifdef Y4
    // motor[0] = PIDMIX(+0,+1,-1); //REAR_1 CW
    // motor[1] = PIDMIX(-1,-1, 0); //FRONT_R CCW
    // motor[2] = PIDMIX(+0,+1,+1); //REAR_2 CCW
    // motor[3] = PIDMIX(+1,-1, 0); //FRONT_L CW
    // #endif
    // #ifdef Y6
    // motor[0] = PIDMIX(+0,+4/3,+1); //REAR
    // motor[1] = PIDMIX(-1,-2/3,-1); //RIGHT
    // motor[2] = PIDMIX(+1,-2/3,-1); //LEFT
    // motor[3] = PIDMIX(+0,+4/3,-1); //UNDER_REAR
    // motor[4] = PIDMIX(-1,-2/3,+1); //UNDER_RIGHT
    // motor[5] = PIDMIX(+1,-2/3,+1); //UNDER_LEFT
    // #endif
    // #ifdef HEX6
    // motor[0] = PIDMIX(-1/2,+1/2,+1); //REAR_R
    // motor[1] = PIDMIX(-1/2,-1/2,-1); //FRONT_R
    // motor[2] = PIDMIX(+1/2,+1/2,+1); //REAR_L
    // motor[3] = PIDMIX(+1/2,-1/2,-1); //FRONT_L
    // motor[4] = PIDMIX(+0 ,-1 ,+1); //FRONT
    // motor[5] = PIDMIX(+0 ,+1 ,-1); //REAR
    // #endif
    // #ifdef HEX6X
    // motor[0] = PIDMIX(-1/2,+1/2,+1); //REAR_R
    // motor[1] = PIDMIX(-1/2,-1/2,+1); //FRONT_R
    // motor[2] = PIDMIX(+1/2,+1/2,-1); //REAR_L
    // motor[3] = PIDMIX(+1/2,-1/2,-1); //FRONT_L
    // motor[4] = PIDMIX(-1 ,+0 ,-1); //RIGHT
    // motor[5] = PIDMIX(+1 ,+0 ,+1); //LEFT
    // #endif
    // #ifdef OCTOX8
    // motor[0] = PIDMIX(-1,+1,-1); //REAR_R
    // motor[1] = PIDMIX(-1,-1,+1); //FRONT_R
    // motor[2] = PIDMIX(+1,+1,+1); //REAR_L
    // motor[3] = PIDMIX(+1,-1,-1); //FRONT_L
    // motor[4] = PIDMIX(-1,+1,+1); //UNDER_REAR_R
    // motor[5] = PIDMIX(-1,-1,-1); //UNDER_FRONT_R
    // motor[6] = PIDMIX(+1,+1,-1); //UNDER_REAR_L
    // motor[7] = PIDMIX(+1,-1,+1); //UNDER_FRONT_L
    // #endif
    // #ifdef OCTOFLATP
    // motor[0] = PIDMIX(+7/10,-7/10,+1); //FRONT_L
    // motor[1] = PIDMIX(-7/10,-7/10,+1); //FRONT_R
    // motor[2] = PIDMIX(-7/10,+7/10,+1); //REAR_R
    // motor[3] = PIDMIX(+7/10,+7/10,+1); //REAR_L
    // motor[4] = PIDMIX(+0 ,-1 ,-1); //FRONT
    // motor[5] = PIDMIX(-1 ,+0 ,-1); //RIGHT
    // motor[6] = PIDMIX(+0 ,+1 ,-1); //REAR
    // motor[7] = PIDMIX(+1 ,+0 ,-1); //LEFT
    // #endif
    // #ifdef OCTOFLATX
    // motor[0] = PIDMIX(+1 ,-1/2,+1); //MIDFRONT_L
    // motor[1] = PIDMIX(-1/2,-1 ,+1); //FRONT_R
    // motor[2] = PIDMIX(-1 ,+1/2,+1); //MIDREAR_R
    // motor[3] = PIDMIX(+1/2,+1 ,+1); //REAR_L
    // motor[4] = PIDMIX(+1/2,-1 ,-1); //FRONT_L
    // motor[5] = PIDMIX(-1 ,-1/2,-1); //MIDFRONT_R
    // motor[6] = PIDMIX(-1/2,+1 ,-1); //REAR_R
    // motor[7] = PIDMIX(+1 ,+1/2,-1); //MIDREAR_L
    // #endif
    // #ifdef VTAIL4
    // motor[0] = PIDMIX(+0,+1, -1/2); //REAR_R
    // motor[1] = PIDMIX(-1, -1, +0); //FRONT_R
    // motor[2] = PIDMIX(+0,+1, +1/2); //REAR_L
    // motor[3] = PIDMIX(+1, -1, -0); //FRONT_L
    // #endif
    void drawBarValue(Graphics2D g2d) {

        g2d.setStroke(new BasicStroke(1));

        switch (uavType) {
            case 2:
            // ifdef QUADP
            // motor[0]//REAR
            // motor[1]//RIGHT
            // motor[2]//LEFT
            // motor[3]//FRONT
            {
                final int[] startx = { 76, 126, 26, 76 };
                final int[] starty = { 179, 129, 129, 79 };
                drawMotorBar(g2d, startx, starty);
            }
                break;
            case 3:
            // #ifdef QUADX
            // motor[0]//REAR_R
            // motor[1]//FRONT_R
            // motor[2]//REAR_L
            // motor[3] //FRONT_L
            {
                final int[] startx = { 121, 121, 41, 41 };
                final int[] starty = { 169, 79, 169, 79 };
                drawMotorBar(g2d, startx, starty);
            }
                break;

            default:
                break;
        }

    }

    private void drawMotorBar(Graphics2D g2d, int[] startx, int[] starty) {
        g2d.setPaint(StyleColor.greenBar);

        for (int i = 0; i < startx.length; i++) {

            int barvalue = new Double(((motor[i] - 1000) / 1000) * yy)
                    .intValue();
            if (barvalue < 0) {
                barvalue = 0;
            }
            final GeneralPath bar = new GeneralPath(Path2D.WIND_EVEN_ODD);
            bar.moveTo(startx[i], starty[i]);
            bar.lineTo(startx[i], starty[i] - barvalue);
            bar.lineTo(startx[i] + xx, starty[i] - barvalue);
            bar.lineTo(startx[i] + xx, starty[i]);
            bar.closePath();

            g2d.fill(bar);

        }

    }

    private void drawUAV(Graphics2D g2d) {

        // int w = 200;

        final BufferedImage bi = new BufferedImage(getMaxRadiusY(),
                getMaxRadiusY(), BufferedImage.TYPE_INT_ARGB);
        final Graphics g = bi.getGraphics();
        g.drawImage(images[uavType], 0, 0, null);

        // float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
        // float[] offsets = new float[4];
        // RescaleOp rop = new RescaleOp(scales, offsets, null);

        g2d.drawImage(bi, null, 0, 0);

    }

    public int getUavType() {
        return uavType;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawUAV(g2d);
        drawBarValue(g2d);

    }

    @Override
    public void readNewValue(Integer string, int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readNewValue(String name, Double value) {
        motor[Integer.parseInt(name.charAt(name.length() - 1) + "")] = value;
        repaint();
    }

    @Override
    void resetAllValuesImpl() {

    }

    public void setUavType(int uAVTYPE) {
        uavType = uAVTYPE;
        repaint();
    }

}
