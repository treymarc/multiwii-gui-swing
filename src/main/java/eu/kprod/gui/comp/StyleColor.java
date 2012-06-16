package eu.kprod.gui.comp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StyleColor {
    
    public static final List<Color> colors = initializeMap();
  
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
        m.add(Color.WHITE);
        m.add(Color.BLACK);
        m.add(Color.DARK_GRAY);
        return Collections.unmodifiableList(m);
    }

    
    public static final Color backGround = Color.gray;
   
    // public static Color forGround= Color.white;

    public static Color getColor(int l) {
        // TODO Auto-generated method stub
        return colors.get(l);
    }
}
