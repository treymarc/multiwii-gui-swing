/**
 * @author treym (Trey Marc) Jun 16 2012
 *
 */
package eu.kprod.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.GeneralPath;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.msp.MSP;

public class MwCompasPanel extends MwInstrumentJPanel implements
        MwDataSourceListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Ellipse2D roundHorizon;

    private GeneralPath bankMarkerLong;
    private GeneralPath bankMarkerShort;

    private Integer head = 0;

    private GeneralPath triangle;

    private Double alt = 0.0;

    public MwCompasPanel(Color c) {
        super();
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

        roundHorizon = new Ellipse2D.Float((maxRadius - radius * 2) / 2,
                (maxRadius - radius * 2) / 2, 2 * radius, 2 * radius);

        g2d.setStroke(new BasicStroke(3));
        g2d.draw(roundHorizon);

    }

    private void drawBackground(Graphics2D g2d) {

        Float background = new Ellipse2D.Float((maxRadius - radius * 2) / 2,
                (maxRadius - radius * 2) / 2, 2 * radius, 2 * radius);

        g2d.setPaint(Color.DARK_GRAY);
        g2d.fill(background);

    }

    void drawValue(Graphics2D g2d) {

        g2d.setFont(writing);
        g2d.setPaint(Color.white);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString("Alt " + (alt), 10, 10);

    }

    private void drawCompas(Graphics2D g2d) {
        // rotate to heading
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(-head), centerPoint.getX(), centerPoint.getY());
        g2d.transform(at);

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(Color.white);


        bankMarkerShort = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        bankMarkerShort.moveTo((centerPoint.getX() - radius),
                centerPoint.getY());
        bankMarkerShort.lineTo((centerPoint.getX() - radius + 6),
                centerPoint.getY());

        AffineTransform ata = AffineTransform.getRotateInstance(
                Math.toRadians(22.5), centerPoint.getX(), centerPoint.getY());

        for (int i = 0; i < 16; i++) {

            if (i==0) {
                // W
                bankMarkerLong = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                bankMarkerLong.moveTo((centerPoint.getX() - radius ),           centerPoint.getY()-10);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 16),       centerPoint.getY()-5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius +3),           centerPoint.getY());
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 16),       centerPoint.getY()+5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius ),           centerPoint.getY()+10);

                g2d.setStroke(new BasicStroke(2));
                g2d.draw(bankMarkerLong);
            }else if (i==4) {
 // N
                bankMarkerLong = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                bankMarkerLong.moveTo((centerPoint.getX() - radius -3),           centerPoint.getY()-5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 14),       centerPoint.getY()-5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius ),           centerPoint.getY()+5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 15),       centerPoint.getY()+5);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(bankMarkerLong);
            }else if (i==8) {
                
                // E
                bankMarkerLong = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                bankMarkerLong.moveTo((centerPoint.getX() - radius ),           centerPoint.getY()+6);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 5),       centerPoint.getY()-6);
                bankMarkerLong.lineTo((centerPoint.getX() - radius +9),           centerPoint.getY()+6);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 13),       centerPoint.getY()-6);
                bankMarkerLong.lineTo((centerPoint.getX() - radius +17),           centerPoint.getY()-6);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(bankMarkerLong);
            }else if (i==12) {
                // S
                bankMarkerLong = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                bankMarkerLong.moveTo((centerPoint.getX() - radius ),           centerPoint.getY()-5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 6),       centerPoint.getY()+5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius + 12),       centerPoint.getY()-5);
                bankMarkerLong.lineTo((centerPoint.getX() - radius +18),           centerPoint.getY()+5);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(bankMarkerLong);
            }else {
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(bankMarkerShort);
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

        triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        triangle.moveTo(centerPoint.getX(), (centerPoint.getY() - radius + 5));
        triangle.lineTo((centerPoint.getX() - 15),
                (centerPoint.getY() - radius + 30));
        triangle.lineTo((centerPoint.getX() + 15),
                (centerPoint.getY() - radius + 30));
        triangle.closePath();

        g2d.fill(triangle);

        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(Color.white);

        String k =  head.toString();
        
            g2d.drawString(k, (float) (centerPoint.getX() - k.length() * 3),
                    (float) (centerPoint.getY() - (radius - 40)));
        
    }

    @Override
    public void readNewValue(String name, Double value) {
        if (MSP.IDhead.equals(name)) {
            this.head = value.intValue();
        } else if (MSP.IDalt.equals(name)) {
            this.alt = value;
        }

        repaint();
    }
}
