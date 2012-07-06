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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import eu.kprod.ds.MwSensorClass;
import eu.kprod.gui.MwConfiguration;
import eu.kprod.gui.MwGuiRuntimeException;
import eu.kprod.msp.MSP;

public class MwRCDataPanel extends MwInstrumentJPanel {

    private Image imageRCdataeBg= super.getImage("rcdata.png");
    
    

    private static final int rcDatabarWidth = 7;
    
    private int[] startx=initializePositionX();

    private int[] starty=initializePositionY();


    private static int[] initializePositionY() {
        final int[] m = new int[8];
        int starty = 16;

        for (int i = 0; i < m.length; i++) {
            
            m[i]=starty;
              starty += rcDatabarWidth + 8;
        }

        return m;
    }
    
    private static int[] initializePositionX() {
        final int[] m = new int[8];
       
        
        for (int i = 0; i < m.length; i++) {
            m[i]=41;
        }

        return m;
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final double[] dataRC = new double[8];


    {
        if (imageRCdataeBg == null) {

            final URL url = this.getClass().getResource(MwConfiguration.getPath(MwConfiguration.IMAGE)+"/"+"rcdata.png");
            
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
        super.setBarMax(118);
        super.setBarWidth(rcDatabarWidth);

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

    
    

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);

        drawBar(g2d, 2, dataRC, null, startx, starty, XAXIS);

    }

    @Override
    public void readNewValue(final Integer string, final int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readNewValue(Class<? extends MwSensorClass> sensorClass, final String name, final Double value) {

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
