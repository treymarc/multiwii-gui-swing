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
import java.awt.image.BufferedImage;
import java.net.URL;

import eu.kprod.gui.comp.StyleColor;
import eu.kprod.msp.MSP;

public  class MwUAVPanel extends MwInstrumentJPanel  {

    protected GeneralPath bar;

    double[] RCmotor = new double[8];
    static Image [] images = new Image[14];

    protected int UAVTYPE=10;

    {
       

            try {
                for (int i = 1; i < images.length; i++) {
                    
                    URL  urlfw= this.getClass().getResource( "/uav/"+i+".png");

                    images[i] = Toolkit.getDefaultToolkit().getImage(urlfw);
                }

            } catch (Exception e) {
                System.out.println("resources not found!!!");
            }
        
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;



    public MwUAVPanel(Color c) {
        super(new Dimension(170,200));

        setBackground(c);


    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawUAV(g2d);
        drawBarValue(g2d);


    }


    private void drawUAV(Graphics2D g2d) {

        //            int w = 200;

        BufferedImage bi = new
                BufferedImage(maxRadiusY, maxRadiusY, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(images[UAVTYPE], 0, 0, null);


        //            float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
        //            float[] offsets = new float[4];
        //            RescaleOp rop = new RescaleOp(scales, offsets, null);

        g2d.drawImage(bi, null, 0 ,  0);

    }


    void drawBarValue(Graphics2D g2d) {
        final int startx = 41;
        int starty = 16;

        // bar w/h
        final int xx = 118;
        final int yy = 7;

        g2d.setStroke(new BasicStroke(1));
        
        g2d.setPaint(StyleColor.greenBar);
        for (int i = 0; i < RCmotor.length; i++) {

            int barvalue = new Double((RCmotor[i]/2000)*xx).intValue();
            bar = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            bar.moveTo(startx, starty);
            bar.lineTo(startx + barvalue, starty);
            bar.lineTo(startx + barvalue, starty + yy);
            bar.lineTo(startx, starty + yy);
            bar.closePath();

            g2d.fill(bar);
            starty+=yy+8;

        }

    }
    @Override
    public void readNewValue(String name, Double value) {     
        RCmotor[Integer.parseInt(name.charAt(name.length()-1)+"")]=value;
        repaint();
    }

   
    
    @Override
    void resetAllValuesImpl() {

    }


}
