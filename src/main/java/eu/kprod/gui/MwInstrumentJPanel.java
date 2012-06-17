package eu.kprod.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;

import eu.kprod.gui.comp.MwJPanel;

public abstract class MwInstrumentJPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    static Font writing = null;

    Point2D centerPoint;
    int radius;
    int maxRadius = 220;
    Dimension dimPanel;

     int dimMarker5Deg;
     int dimMarker10Deg;

    
    public MwInstrumentJPanel() {

        // Instance variables initialization
        this.radius = ((Double) (0.45 * this.maxRadius)).intValue();
        dimPanel = new Dimension(this.maxRadius, this.maxRadius);

        // Define a center point as a reference
        this.centerPoint = new Point2D.Float(this.maxRadius / 2,
                this.maxRadius / 2);

        if (writing == null) {

            InputStream is = this.getClass().getResourceAsStream(
                    "/01Digitall.ttf");

            try {
                writing = Font.createFont(Font.TRUETYPE_FONT, is);

                writing = writing.deriveFont(12.0f);

            } catch (FontFormatException e) {
                System.out.println("Format fonts not correct!!!");
            } catch (IOException e) {
                System.out.println("Fonts not found!!!");
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return dimPanel;
    }

}
