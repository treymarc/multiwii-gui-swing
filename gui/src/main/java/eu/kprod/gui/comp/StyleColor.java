package eu.kprod.gui.comp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class StyleColor {
    
    public static final List<Color> colors = initializeMap();

    public static final Color blueSky = new Color(10, 112, 156);
    public static final Color orangeEarth = new Color(222, 132, 14);
   
    public static final Color greenBar = new Color(96, 220, 113);
    
    private static List<Color> initializeMap() {
        List<Color> m = new ArrayList<Color>();
        m.add(Color.BLUE);
        m.add(Color.GREEN);
        m.add(Color.YELLOW);
        m.add(Color.PINK);
        m.add(Color.RED);
        m.add(Color.CYAN);
        m.add(Color.MAGENTA);
        m.add(Color.ORANGE);
        m.add(StyleColor.forGround);
        m.add(Color.BLACK);
        m.add(Color.DARK_GRAY);
        
        m.add(new Color(250,100,100));
        m.add(new Color(250,200,100));
        m.add(new Color(250,100,200));
        m.add(new Color(250,200,200));
        
        return m;
    }

    
    public static final Color backGround = new Color(51,51,51);
    public static final Color forGround = new Color(245,245,240);
    
    // public static Color forGround= StyleColor.forGround;

    public static Color getColor(int l) {
        // TODO Auto-generated method stub
        return colors.get(l);
    }

    public static void setLookAndFeel() {
        // TODO remember OS
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

            } catch (Exception e) {
                // continue
            }
        }
        
    }

    public static void setColor(int index, Color color) {
        colors.set(index, color);
        
    }
}
