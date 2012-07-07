/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package eu.kprod.gui.comp;

import java.awt.Color;


public abstract class MwColor {

    public static final int COLORSET = 0;
    public static final int COLORSETGRAPH = 1;
    
    public static final int BACKGROUND_COLOR = 0;
    public static final int FORGROUND_COLOR = 1;
    public static final int INSTR_SKY_BLUE = 2;
    public static final int INSTR_BAR_GREEN = 3;
    public static final int INSTR_EARTH_ORANGE = 4;
    public static final int INSTR_BAR_RED = 5;
    public static final int INSTR_BAR_YELLOW = 6;
    

    public Color getColorGraph(int colorKey){
        return getColorImpl(COLORSETGRAPH, colorKey);
    }
    
    public Color getColor(int colorKey){
        return getColorImpl(COLORSET, colorKey);
    }
    
    abstract Color getColorImpl(int set, int colorKey);
    
    abstract void setColorGraphImpl(int index, Color color);
    
    public void setColorGraph(int index, Color color){
        setColorGraphImpl( index,  color);
    }
    
}
