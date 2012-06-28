/**
 * @author treym (Trey Marc) Jun 16 2012
 *
 */
package eu.kprod.gui.instrument;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.net.URL;

import eu.kprod.gui.Ress;
import eu.kprod.gui.comp.StyleColor;
import eu.kprod.msp.MSP;

public class MwCompasPanel extends MwInstrumentJPanel  {

    {
        if (imageCompas == null) {

            URL  url = this.getClass().getResource(Ress.imgCompas);


            try {
                imageCompas = Toolkit.getDefaultToolkit().getImage(url);


            } catch (Exception e) {
                System.err.println("Fonts not found!!!");
            }
        }
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

//    private Ellipse2D roundHorizon;

//    private GeneralPath bankMarkerLong;
//    private GeneralPath bankMarkerShort;

//  private GeneralPath triangle;

    private Integer head = 0;
    private Double alt = 0.0;

    private static Image imageCompas;

    public MwCompasPanel(Color c) {
        super(null);
        setBackground(c);

       
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawValue(g2d);
        drawBackground(g2d);
        drawCompas(g2d);

        drawUAV(g2d);

        Float roundHorizon = new Ellipse2D.Float((maxRadiusX - radiusx * 2) / 2,
                (maxRadiusY - radiusy * 2) / 2, 2 * radiusx, 2 * radiusy);

        g2d.setStroke(new BasicStroke(3));
        g2d.draw(roundHorizon);

    }

    private void drawBackground(Graphics2D g2d) {

        Float background = new Ellipse2D.Float((maxRadiusX - radiusx * 2) / 2,
                (maxRadiusY - radiusy * 2) / 2, 2 * radiusx, 2 * radiusy);

        g2d.setPaint(Color.DARK_GRAY);
        g2d.fill(background);

    }

    void drawValue(Graphics2D g2d) {

        g2d.setFont(writing);
        g2d.setPaint(StyleColor.forGround);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString("Alt " + (alt), 10, 10);

    }

    private void drawCompas(Graphics2D g2d) {
        // rotate to heading
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(-head), centerPoint.getX(), centerPoint.getY());
        g2d.transform(at);

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(StyleColor.forGround);


        GeneralPath bankMarker = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        bankMarker.moveTo((centerPoint.getX() - radiusx),
                centerPoint.getY());
        bankMarker.lineTo((centerPoint.getX() - radiusx + 6),
                centerPoint.getY());

        AffineTransform ata = AffineTransform.getRotateInstance(
                Math.toRadians(22.5), centerPoint.getX(), centerPoint.getY());

        for (int i = 0; i < 16; i++) {

            GeneralPath letterPath;
            if (i==0) {
                // W
                letterPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                letterPath.moveTo((centerPoint.getX() - radiusx +4),           centerPoint.getY()-6);
                letterPath.lineTo((centerPoint.getX() - radiusx +8),       centerPoint.getY()+6);
                letterPath.lineTo((centerPoint.getX() - radiusx +12),           centerPoint.getY()-4);
                letterPath.lineTo((centerPoint.getX() - radiusx +16 ),       centerPoint.getY()+6);
                letterPath.lineTo((centerPoint.getX() - radiusx +20 ),           centerPoint.getY()-6);

                g2d.setStroke(new BasicStroke(2));
                g2d.draw(letterPath);
            }else if (i==4) {
                   // N
                letterPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                letterPath.moveTo((centerPoint.getX() - radiusx + 4 ),           centerPoint.getY()-6);
                letterPath.lineTo((centerPoint.getX() - radiusx + 19),       centerPoint.getY()-6);
                letterPath.lineTo((centerPoint.getX() - radiusx + 4 ),           centerPoint.getY()+6);
                letterPath.lineTo((centerPoint.getX() - radiusx + 20),       centerPoint.getY()+6);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(letterPath);
            }else if (i==8) {
                
                // E
                letterPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                letterPath.moveTo((centerPoint.getX() - radiusx +6),        centerPoint.getY()+7);
                letterPath.lineTo((centerPoint.getX() - radiusx +16),       centerPoint.getY()+7);
                letterPath.lineTo((centerPoint.getX() - radiusx +16),       centerPoint.getY());
                letterPath.lineTo((centerPoint.getX() - radiusx +10),      centerPoint.getY());
                letterPath.lineTo((centerPoint.getX() - radiusx +16),       centerPoint.getY());

                letterPath.lineTo((centerPoint.getX() - radiusx +16),       centerPoint.getY()-7);
                letterPath.lineTo((centerPoint.getX() - radiusx +6),       centerPoint.getY()-7);

                g2d.setStroke(new BasicStroke(2));
                g2d.draw(letterPath);
            }else if (i==12) {
                // S
                letterPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                letterPath.moveTo((centerPoint.getX() - radiusx +3),           centerPoint.getY()-5);
                letterPath.curveTo(
                        (centerPoint.getX() - radiusx +12),       centerPoint.getY()+25, 
                        (centerPoint.getX() - radiusx +12),       centerPoint.getY()-25,
                        (centerPoint.getX() - radiusx +20),       centerPoint.getY()+5);
                        
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(letterPath);
            }else {
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(bankMarker);
            }

            g2d.transform(ata);

        }

        at = AffineTransform.getRotateInstance(Math.toRadians(head),
                centerPoint.getX(), centerPoint.getY());
        g2d.transform(at);

    }

    private void drawUAV(Graphics2D g2d) {

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(Color.lightGray);

//        triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
//        triangle.moveTo(centerPoint.getX(), (centerPoint.getY() - radius + 5));
//        triangle.lineTo((centerPoint.getX() - 15),
//                (centerPoint.getY() - radius + 30));
//        triangle.lineTo((centerPoint.getX() + 15),
//                (centerPoint.getY() - radius + 30));
//        triangle.closePath();
//
//        g2d.fill(triangle);

        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(StyleColor.forGround);

        String k =  head.toString();
        
            g2d.drawString(k, (float) (centerPoint.getX() - k.length() * 3),
                    (float) centerPoint.getY());
            
            int w = 200;

            BufferedImage bi = new
                BufferedImage(maxRadiusY, maxRadiusY, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.getGraphics();
            g.drawImage(imageCompas, 0, 0, null);


            float[] scales = { 1.0f ,1.0f,1.0f,0.8f};
            float[] offsets = new float[4];
            RescaleOp rop = new RescaleOp(scales, offsets, null);


            g2d.drawImage(bi, rop, (maxRadiusX-w )/ 2 ,  (maxRadiusY-w )/ 2);

            
          
//            g2d.draw(triangle);
            
    }

    @Override
    public void readNewValue(String name, Double value) {
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

    @Override
    public void readNewValue(Integer string, int i) {
        // TODO Auto-generated method stub
        
    }

   
}
