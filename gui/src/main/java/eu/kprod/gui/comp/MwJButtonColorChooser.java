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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MwJButtonColorChooser extends MwJButton {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Color color;



    public MwJButtonColorChooser( final int index, String name, Color color1) {
        // TODO Auto-generated constructor stub
        super("", name + " color");
        super.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MwColorChooser.getInstance(index,color);
            }
        });
        setBackground(color1);

        this.color = color1;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color1) {
        this.color = color1;
        setBackground(color);
    }



}
