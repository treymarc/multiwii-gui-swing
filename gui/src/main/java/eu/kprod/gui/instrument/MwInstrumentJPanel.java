package eu.kprod.gui.instrument;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.gui.Ress;
import eu.kprod.gui.comp.MwJPanel;

public abstract class MwInstrumentJPanel extends MwJPanel implements
        MwDataSourceListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    static Font writing = null;

    Point2D centerPoint;
    int radiusx;
    int radiusy;
    int maxRadiusX = 200;
    int maxRadiusY = 200;

    Dimension dimPanel;

    int dimMarker5Deg=7;
    int dimMarker10Deg=15;

    public MwInstrumentJPanel(Dimension dimension) {
        if (dimension == null) {
            // Instance variables initialization
            dimPanel = new Dimension(this.maxRadiusX, this.maxRadiusY);
        } else {
            dimPanel = dimension;
            this.maxRadiusX = dimPanel.width;
            this.maxRadiusY = dimPanel.height;
        }
        this.radiusx = ((Double) (0.45 * this.maxRadiusX)).intValue();
        this.radiusy = ((Double) (0.45 * this.maxRadiusY)).intValue();
        
        // this.setMinimumSize(dimPanel);
        // Define a center point as a reference
        this.centerPoint = new Point2D.Float(this.maxRadiusX / 2,
                this.maxRadiusY / 2);

        if (writing == null) {

            InputStream is = this.getClass().getResourceAsStream(
                    Ress.font);

            try {
                writing = Font.createFont(Font.TRUETYPE_FONT, is);

                writing = writing.deriveFont(12.0f);

            } catch (FontFormatException e) {
                System.err.println("Format fonts not correct!!!");
            } catch (IOException e) {
                System.err.println("Fonts not found!!!");
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return dimPanel;
    }

    @Override
    public void resetAllValues() {
        resetAllValuesImpl();
        repaint();

    }

    abstract void resetAllValuesImpl();


}
