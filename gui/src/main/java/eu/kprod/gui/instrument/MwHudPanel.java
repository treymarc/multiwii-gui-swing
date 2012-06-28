/**
 * @author wilhem (Davide Picchi) Feb 24, 2010
 * @author treym (Trey Marc) Jun 16 2012
 *
 */
package eu.kprod.gui.instrument;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import eu.kprod.gui.comp.StyleColor;
import eu.kprod.msp.MSP;

public class MwHudPanel extends MwInstrumentJPanel  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Arc2D upperArc; // Upper part of the Horizon
    private Arc2D lowerArc; // Bottom part of the Horizon

    private int rollAngle;
    private int pitchAngle;

    public MwHudPanel(Color c) {
        super(null);
        setBackground(c);

        // Creates two arcs used to draw the outline
        upperArc = new Arc2D.Float();
        lowerArc = new Arc2D.Float();

//        super.dimMarker10Deg = 15;
//        super.dimMarker5Deg = 7;

    }


    /****************************
     * Main paintComponent method
     ***************************/
    public void paintComponent(Graphics g) {
       
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawValue(g2d);
        drawHorizon(g2d);

        g2d.setStroke(new BasicStroke(2));
        g2d.setPaint(StyleColor.forGround);

        // Draw the Bank roll lines on the top
        drawBankRollMarker(g2d);

        radiusx = ((Double) (0.45 * maxRadiusX)).intValue();
        radiusy = ((Double) (0.45 * maxRadiusY)).intValue();
        Ellipse2D roundHorizon = new Ellipse2D.Float((maxRadiusX - radiusx * 2) / 2,
                (maxRadiusY - radiusy * 2) / 2, 2 * radiusx, 2 * radiusy);

        g2d.setStroke(new BasicStroke(3));
        g2d.draw(roundHorizon);

    }

    void drawValue(Graphics2D g2d) {
        // TODO Auto-generated method stub

        g2d.setFont(writing);
        g2d.setPaint(StyleColor.forGround);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString("X " + (-rollAngle), 10, maxRadiusY - 20);

        g2d.setFont(writing);
        g2d.setPaint(StyleColor.forGround);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString("Y " + (-pitchAngle), 10, maxRadiusY - 10);
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

            angStartUpper = -(180 - pitchAngle); // Minus because of the reverse
                                                 // way of working of the
                                                 // artificial horizon positive
                                                 // values let the blue arc to
                                                 // get bigger...
            angExtUpper = (180 - (2 * angStartUpper));
        }

        if ((pitchAngle <= -90) && (pitchAngle > -180)) {

            angStartUpper = (180 + pitchAngle); // Minus because of the reverse
                                                // way of working of the
                                                // artificial horizon positive
                                                // values let the blue arc to
                                                // get bigger...
            angExtUpper = (180 - (2 * angStartUpper));
        }

        // Draw the artificial horizon itself, composed by 2 half arcs
        lowerArc.setArcByCenter(centerPoint.getX(), centerPoint.getY(), radiusy,
                angStartLower, angExtLower, Arc2D.CHORD);
        g2d.setPaint(StyleColor.orangeEarth);
        g2d.fill(lowerArc);

        upperArc.setArcByCenter(centerPoint.getX(), centerPoint.getY(), radiusy,
                angStartUpper, angExtUpper, Arc2D.CHORD);
        g2d.setPaint(StyleColor.blueSky);
        g2d.fill(upperArc);

        // Draw the middle white line
        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(StyleColor.forGround);
        g2d.draw(upperArc);

        drawLines(g2d);

    }

    private void drawLines(Graphics2D g2d) {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(-rollAngle), centerPoint.getX(),
                centerPoint.getY());

        g2d.transform(at);

        // Draw the center shape
        GeneralPath centerShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        centerShape.moveTo((centerPoint.getX() - radiusx / 2.5),
                centerPoint.getY());
        centerShape.lineTo((centerPoint.getX() - 15), centerPoint.getY());
        centerShape.moveTo((centerPoint.getX() - 30), centerPoint.getY());
        centerShape
                .lineTo((centerPoint.getX() - 10), (centerPoint.getY() + 10));
        centerShape.lineTo(centerPoint.getX(), centerPoint.getY());
        centerShape
                .lineTo((centerPoint.getX() + 10), (centerPoint.getY() + 10));
        centerShape.lineTo((centerPoint.getX() + 30), centerPoint.getY());
        centerShape.moveTo((centerPoint.getX() + radiusx / 2.5),
                centerPoint.getY());
        centerShape.lineTo((centerPoint.getX() + 15), centerPoint.getY());

        g2d.setPaint(StyleColor.forGround);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(centerShape);

        int angle;
        int distance;
        int angleCorrUp;
        int limitInf, limitMax;
        Integer ppitchangle = this.pitchAngle;

        limitInf = (int) ((ppitchangle / 10) - 5);
        if (limitInf < -18){
            limitInf = -18;
        }
        limitMax = limitInf + 11;
        if (limitMax > 18){
            limitMax = 19;
        }
        for (int i = limitInf; i < limitMax; i++) {

            angle = i * 10; // Display the text at the right "height"
            angleCorrUp = angle - ppitchangle;
            distance = Math.abs(i * 5); // Put the text and the lines length at
                                        // the right position

            g2d.setPaint(StyleColor.forGround);
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(writing);

            // Longer markers
            Line2D markerLine = new Line2D.Float((float) (centerPoint.getX()
                    - dimMarker10Deg - distance),
                    (float) (centerPoint.getY() - (radiusy * Math.sin(Math
                            .toRadians(angleCorrUp)))),
                    (float) (centerPoint.getX() + dimMarker10Deg + distance),
                    (float) (centerPoint.getY() - (radiusy * Math.sin(Math
                            .toRadians(angleCorrUp)))));

            g2d.draw(markerLine);

            // Short markers
            markerLine = new Line2D.Float(
                    (float) (centerPoint.getX() - dimMarker5Deg),
                    (float) (centerPoint.getY() - (radiusy * Math.sin(Math
                            .toRadians(angleCorrUp + 5)))),
                    (float) (centerPoint.getX() + dimMarker5Deg),
                    (float) (centerPoint.getY() - (radiusy * Math.sin(Math
                            .toRadians(angleCorrUp + 5)))));

            g2d.draw(markerLine);

            // Writing routine
            g2d.drawString(
                    "" + (angle),
                    (float) (centerPoint.getX() - dimMarker10Deg - distance - 25),
                    (float) (centerPoint.getY() - (radiusy
                            * Math.sin(Math.toRadians(angleCorrUp)) - 5)));
            g2d.drawString(
                    "" + (angle),
                    (float) (centerPoint.getX() + dimMarker10Deg + distance + 8),
                    (float) (centerPoint.getY() - (radiusy
                            * Math.sin(Math.toRadians(angleCorrUp)) - 5)));

        }

    }

    private void drawBankRollMarker(Graphics2D g2d) {

        // Draw the line markers for bank angle
        GeneralPath bankMarkerLong = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        bankMarkerLong
                .moveTo((centerPoint.getX() - radiusx), centerPoint.getY());
        bankMarkerLong.lineTo((centerPoint.getX() - radiusx + 6),
                centerPoint.getY());

        AffineTransform ata = AffineTransform.getRotateInstance(
                Math.toRadians(150), centerPoint.getX(), centerPoint.getY());
        g2d.transform(ata);

        for (int i = 0; i < 5; i++) {
            ata = AffineTransform.getRotateInstance(Math.toRadians(10),
                    centerPoint.getX(), centerPoint.getY());
            g2d.transform(ata);

            g2d.draw(bankMarkerLong);

        }

        ata = AffineTransform.getRotateInstance(Math.toRadians(130),
                centerPoint.getX(), centerPoint.getY());
        g2d.transform(ata);

        for (int i = 0; i < 5; i++) {
            ata = AffineTransform.getRotateInstance(Math.toRadians(10),
                    centerPoint.getX(), centerPoint.getY());
            g2d.transform(ata);

            g2d.draw(bankMarkerLong);

        }

    }

    @Override
    public void readNewValue(String name, Double value) {
        if (MSP.IDangy.equals(name)) {

            this.pitchAngle = -value.intValue();
        }

        if (MSP.IDangx.equals(name)) {
            this.rollAngle = -value.intValue();
        }
        repaint();
    }


    @Override
    void resetAllValuesImpl() {
        // TODO Auto-generated method stub
        rollAngle= 0;
        pitchAngle=0;
    }


    @Override
    public void readNewValue(Integer string, int i) {
        // TODO Auto-generated method stub
        
    }


}
