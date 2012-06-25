package eu.kprod;

import javax.swing.SwingUtilities;

import eu.kprod.gui.MwGuiFrame;
import eu.kprod.gui.comp.StyleColor;
//import eu.kprod.serial.SerialException;

/**
 * Known issues
 * 
 * - when zooming the chart : news values are still recorded so due to the
 * dataSource maxItemcounts and AgeLimite , the chart gets emptied at the zoomed
 * date
 * 
 * @author treym
 * 
 */
public class MwGui {

    /**
     * @param args
     * @throws SerialException
     */
    public static void main(String[] args) {
        

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StyleColor.setLookAndFeel();
                MwGuiFrame.getInstance().setVisible(true);
                MwGuiFrame.getInstance().repaint();

            }

        });

    }

}
