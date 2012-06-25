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

public  class MwUAVPanel extends MwInstrumentJPanel  {

    protected GeneralPath bar;

    double[] RCmotor = new double[8];
    static Image [] images = new Image[14];
    
    // bar w/h
    final int xx = 8;
    final int yy = 67;
    
    protected int UAVTYPE=3;

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
       

      

        g2d.setStroke(new BasicStroke(1));
        
        switch (UAVTYPE) {
            case 3:
                drawType3(g2d);
                break;

            default:
                break;
        }
        


    }
    private void drawType3(Graphics2D g2d) {
        
        int [] startx = {41,121, 41,121};
        int [] starty = {79, 79,169,169};
        
        g2d.setPaint(StyleColor.greenBar);
        for (int i = 0; i < 4; i++) {

            int barvalue = new Double((RCmotor[i]/2000)*xx).intValue();
            bar = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            bar.moveTo(startx[i], starty[i]);
            bar.lineTo(startx[i], starty[i] - barvalue);
            bar.lineTo(startx[i] + xx, starty[i] - barvalue);
            bar.lineTo(startx[i] +xx , starty[i] );
            bar.closePath();

            g2d.fill(bar);
         

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
