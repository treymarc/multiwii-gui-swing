/**
 * @author wilhem (Davide Picchi) Feb 24, 2010
 * @author treym (Trey Marc) Jun 16 2012
 *
 */
package eu.kprod.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.gui.comp.MwJPanel;

public class MwHudPanel extends MwJPanel implements MwDataSourceListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /****************************
     * Defining instance variables
     ****************************/

    private static final Color blueSky= new Color(10, 112, 156);
    private static final Color orangeEarth = new Color(222, 132, 14);


    private Dimension dimPanel;
    private Arc2D upperArc; // Upper part of the Horizon
    private Arc2D lowerArc; // Bottom part of the Horizon
    private Point2D centerPoint;

    private Ellipse2D roundHorizon;
    private int radius;

    private Line2D markerLine;
//    private GeneralPath triangle;
    private GeneralPath centerShape;
    private GeneralPath bankMarkerLong;
//    private GeneralPath bankMarkerShort;

    private Font writing = null;

    private int dimMarker5Deg;
    private int dimMarker10Deg;

    private int rollAngle;
    private int pitchAngle;
    private int maxRadius= 220;

    /************************************
     * This constructor will create the initial panel for the Horizon
     ************************************/

    public MwHudPanel(Color c) {

        setBackground(c);

        // Creates two arcs used to draw the outline
        upperArc = new Arc2D.Float();
        lowerArc = new Arc2D.Float();
   
        // Instance variables initialization
        this.radius = ((Double)(0.45*this.maxRadius)).intValue();

        // Define a center point as a reference
        centerPoint = new Point2D.Float(this.maxRadius/2, this.maxRadius/2);

        this.dimMarker10Deg = 15;
        this.dimMarker5Deg = 7;

        /*****************************************
         * Take resources from the folder for font
         ****************************************/
        InputStream is = this.getClass().getResourceAsStream("/01Digitall.ttf");

        try {
            this.writing = Font.createFont(Font.TRUETYPE_FONT, is);

            this.writing = this.writing.deriveFont(12.0f);

        } catch (FontFormatException e) {
            System.out.println("Format fonts not correct!!!");
        } catch (IOException e) {
            System.out.println("Fonts not found!!!");
        }

    }

    public Dimension getPreferredSize() {
        dimPanel = new Dimension(this.maxRadius, this.maxRadius);
        return dimPanel;
    }

    /****************************
     * Main paintComponent method
     ***************************/
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawHorizon(g2d);

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(Color.white);

        // Draw the Bank roll lines on the top
        drawBankRollMarker(g2d);

        roundHorizon = new Ellipse2D.Float((maxRadius-radius*2)/2, (maxRadius-radius*2)/2, 2 * radius, 2 * radius);

        g2d.setStroke(new BasicStroke(3));
        g2d.draw(roundHorizon);
        
    }

    private void drawHorizon(Graphics2D g2d) {

        // Start doing some math calculation for angles
        int angStartUpper = 0;
        int angExtUpper = 0;
        int angStartLower = 0;
        int angExtLower = 360;

        // First step is to determine the roll display position
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(rollAngle), centerPoint.getX(),
                centerPoint.getY());
        g2d.transform(at);

        if ((pitchAngle < 90) && (pitchAngle > -90)) {
            angStartUpper = -pitchAngle; // Minus because of the reverse way of
                                         // working of the artificial horizon
                                         // positive values let the blue arc to
                                         // get bigger...
            angExtUpper = (180 - (2 * angStartUpper));
        }

        if ((pitchAngle >= 90) && (pitchAngle < 180)) {
            at = AffineTransform.getRotateInstance(Math.toRadians(180),
                    centerPoint.getX(), centerPoint.getY());
            g2d.transform(at);

            angStartUpper = -(180 - pitchAngle); // Minus because of the reverse
                                                 // way of working of the
                                                 // artificial horizon positive
                                                 // values let the blue arc to
                                                 // get bigger...
            angExtUpper = (180 - (2 * angStartUpper));
        }

        if ((pitchAngle <= -90) && (pitchAngle > -180)) {
            at = AffineTransform.getRotateInstance(Math.toRadians(180),
                    centerPoint.getX(), centerPoint.getY());
            g2d.transform(at);

            angStartUpper = (180 + pitchAngle); // Minus because of the reverse
                                                // way of working of the
                                                // artificial horizon positive
                                                // values let the blue arc to
                                                // get bigger...
            angExtUpper = (180 - (2 * angStartUpper));
        }


       
        
        // Draw the artificial horizon itself, composed by 2 half arcs
        lowerArc.setArcByCenter(centerPoint.getX(), centerPoint.getY(), radius,
                angStartLower, angExtLower, Arc2D.CHORD);
        g2d.setPaint(orangeEarth);
        g2d.fill(lowerArc);

        upperArc.setArcByCenter(centerPoint.getX(), centerPoint.getY(), radius,
                angStartUpper, angExtUpper, Arc2D.CHORD);
        g2d.setPaint(blueSky);
        g2d.fill(upperArc);

        // Draw the middle white line
        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(Color.white);
        g2d.draw(upperArc);

        
        at = AffineTransform.getRotateInstance(Math.toRadians(-rollAngle),
                centerPoint.getX(), centerPoint.getY());

        g2d.transform(at);
        drawMarkers(g2d);
  
    }

    private void drawMarkers(Graphics2D g2d) {
        // Draw the lines on the Horizon
        drawLines(g2d);
        // Draw the Bank roll display on the top
        //drawBankRollTriangle(g2d);
    }

    private void drawLines(Graphics2D g2d) {

        int angle;
        int distance;
        int angleCorrUp;
        int limitInf, limitMax;

        limitInf = (int) ((this.pitchAngle / 10) - 5);
        if (limitInf < -18)
            limitInf = -18;
        limitMax = limitInf + 11;
        if (limitMax > 18)
            limitMax = 19;

        for (int i = limitInf; i < limitMax; i++) {

            angle = i * 10; // Display the text at the right "height"
            angleCorrUp = angle - this.pitchAngle;
            distance = Math.abs(i * 5); // Put the text and the lines length at
                                        // the right position

            g2d.setPaint(Color.white);
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(writing);

            // Longer markers
            markerLine = new Line2D.Float((float) (centerPoint.getX()
                    - dimMarker10Deg - distance),
                    (float) (centerPoint.getY() - (radius * Math.sin(Math
                            .toRadians(angleCorrUp)))),
                    (float) (centerPoint.getX() + dimMarker10Deg + distance),
                    (float) (centerPoint.getY() - (radius * Math.sin(Math
                            .toRadians(angleCorrUp)))));

            g2d.draw(markerLine);

            // Short markers
            markerLine = new Line2D.Float(
                    (float) (centerPoint.getX() - dimMarker5Deg),
                    (float) (centerPoint.getY() - (radius * Math.sin(Math
                            .toRadians(angleCorrUp + 5)))),
                    (float) (centerPoint.getX() + dimMarker5Deg),
                    (float) (centerPoint.getY() - (radius * Math.sin(Math
                            .toRadians(angleCorrUp + 5)))));

            g2d.draw(markerLine);

            // Writing routine
            g2d.drawString(
                    "" + (angle),
                    (float) (centerPoint.getX() - dimMarker10Deg - distance - 25),
                    (float) (centerPoint.getY() - (radius
                            * Math.sin(Math.toRadians(angleCorrUp)) - 5)));
            g2d.drawString(
                    "" + (angle),
                    (float) (centerPoint.getX() + dimMarker10Deg + distance + 8),
                    (float) (centerPoint.getY() - (radius
                            * Math.sin(Math.toRadians(angleCorrUp)) - 5)));

        }

        // Draw the center shape
        centerShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        centerShape.moveTo((centerPoint.getX() - radius / 2.5),
                centerPoint.getY());
        centerShape.lineTo((centerPoint.getX() - 15), centerPoint.getY());
        centerShape.moveTo((centerPoint.getX() - 30), centerPoint.getY());
        centerShape
                .lineTo((centerPoint.getX() - 10), (centerPoint.getY() + 10));
        centerShape.lineTo(centerPoint.getX(), centerPoint.getY());
        centerShape
                .lineTo((centerPoint.getX() + 10), (centerPoint.getY() + 10));
        centerShape.lineTo((centerPoint.getX() + 30), centerPoint.getY());
        centerShape.moveTo((centerPoint.getX() + radius / 2.5),
                centerPoint.getY());
        centerShape.lineTo((centerPoint.getX() + 15), centerPoint.getY());

        g2d.setPaint(Color.white);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(centerShape);
    }

//    private void drawBankRollTriangle(Graphics2D g2d) {
//
//        // Draw the triangle on the upper position
//        triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
//        triangle.moveTo(centerPoint.getX(), (centerPoint.getY() - radius + 5));
//        triangle.lineTo((centerPoint.getX() - 7),
//                (centerPoint.getY() - radius + 18));
//        triangle.lineTo((centerPoint.getX() + 7),
//                (centerPoint.getY() - radius + 18));
//        triangle.closePath();
//
//        g2d.fill(triangle);
//
//        // Draw the triangle in the lower position
//        triangle.moveTo(centerPoint.getX(), (centerPoint.getY() + radius - 5));
//        triangle.lineTo((centerPoint.getX() - 7),
//                (centerPoint.getY() + radius - 18));
//        triangle.lineTo((centerPoint.getX() + 7),
//                (centerPoint.getY() + radius - 18));
//        triangle.closePath();
//
//        g2d.draw(triangle);
//    }

    private void drawBankRollMarker(Graphics2D g2d) {

        // Draw the line markers for bank angle
        bankMarkerLong = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        bankMarkerLong
                .moveTo((centerPoint.getX() - radius), centerPoint.getY());
        bankMarkerLong.lineTo((centerPoint.getX() - radius + 6),
                centerPoint.getY());

//        bankMarkerShort = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
//        bankMarkerShort.moveTo((centerPoint.getX() - radius),
//                centerPoint.getY());
//        bankMarkerShort.lineTo((centerPoint.getX() - radius + 5),
//                centerPoint.getY());

        AffineTransform ata = AffineTransform.getRotateInstance(
                Math.toRadians(150), centerPoint.getX(), centerPoint.getY());
        g2d.transform(ata);
        
        for (int i = 0; i < 5; i++) {
            ata = AffineTransform.getRotateInstance(
                    Math.toRadians(10), centerPoint.getX(), centerPoint.getY());
            g2d.transform(ata);

            g2d.draw(bankMarkerLong);

        }

        ata = AffineTransform.getRotateInstance(
                Math.toRadians(130), centerPoint.getX(), centerPoint.getY());
        g2d.transform(ata);

        
        for (int i = 0; i < 5; i++) {
            ata = AffineTransform.getRotateInstance(
                    Math.toRadians(10), centerPoint.getX(), centerPoint.getY());
            g2d.transform(ata);

            g2d.draw(bankMarkerLong);

        }
        
     
       
    }

    @Override
    public void readNewValue(String name, Double value) {
        // TODO Auto-generated method stub
        if ("angy".equals(name)) {
            this.pitchAngle = -value.intValue();
        }

        if ("angx".equals(name)) {
            this.rollAngle = -value.intValue();
        }

        repaint();

    }
}
