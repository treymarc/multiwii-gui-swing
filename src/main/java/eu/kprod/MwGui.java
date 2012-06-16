package eu.kprod;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import eu.kprod.gui.MwGuiFrame;
import eu.kprod.serial.SerialException;

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
        // UIManager.LookAndFeelInfo[] info =
        // UIManager.getInstalledLookAndFeels();
		
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        
		} else {		
	        try {
	            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	                if ("Nimbus".equals(info.getName())) {
	                    UIManager.setLookAndFeel(info.getClassName());
	                    break;
	                }
	            }
	        } catch (UnsupportedLookAndFeelException e) {
	            // handle exception
	        } catch (ClassNotFoundException e) {
	            // handle exception
	        } catch (InstantiationException e) {
	            // handle exception
	        } catch (IllegalAccessException e) {
	            // handle exception
	        }
		}

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MwGuiFrame.getInstance().setVisible(true);

            }

        });

    }

}
