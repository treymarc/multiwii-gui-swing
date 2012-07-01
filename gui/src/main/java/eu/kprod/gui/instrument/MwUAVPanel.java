/**
 * @author treym (Trey Marc) Jun 22 2012
 *
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

    // protected GeneralPath bar;

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

                final URL urlfw = this.getClass().getResource("/uav/" + i + ".png");

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
    void drawBarValue(Graphics2D g2d) {

        g2d.setStroke(new BasicStroke(1));

        switch (uavType) {
            case 2: {
                final int[] startx = { 76, 126, 76, 26 };
                final int[] starty = { 79, 129, 179, 129 };
                drawMotorBar(g2d, startx, starty);
            }
            break;
            case 3: {
                final int[] startx = { 41, 121, 41, 121 };
                final int[] starty = { 79, 79, 169, 169 };
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

        final BufferedImage bi = new BufferedImage(getMaxRadiusY(), getMaxRadiusY(),
                BufferedImage.TYPE_INT_ARGB);
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
