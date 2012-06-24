/**
 * @author treym (Trey Marc) Jun 22 2012
 *
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

public class MwUAVPanel extends MwInstrumentJPanel  {

    {
        if (imageUAV == null) {

            URL  url = this.getClass().getResource( "/uav/fw.png");


            try {
                imageUAV = Toolkit.getDefaultToolkit().getImage(url);


            } catch (Exception e) {
                System.out.println("resources not found!!!");
            }
        }
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;



    private static Image imageUAV;

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



    }


    private void drawUAV(Graphics2D g2d) {

//            int w = 200;

            BufferedImage bi = new
                BufferedImage(maxRadiusY, maxRadiusY, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.getGraphics();
            g.drawImage(imageUAV, 0, 0, null);


//            float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
//            float[] offsets = new float[4];
//            RescaleOp rop = new RescaleOp(scales, offsets, null);

            g2d.drawImage(bi, null, 0 ,  0);
         
    }

    @Override
    public void readNewValue(String name, Double value) {

        repaint();
    }

    @Override
    void resetAllValuesImpl() {

        
    }

   
}
