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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JTextField;

public class MwTextField extends JTextField implements MouseListener,
MouseMotionListener, ActionListener {

    private static final NumberFormat DFORMAT = new DecimalFormat("0.00");
    private static final NumberFormat IFORMAT = new DecimalFormat("0.0000");
    private static final NumberFormat PFORMAT = new DecimalFormat("0.00");

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private NumberFormat format;

    private String previousValue;
    private int previousX;
    private final Double step;
    private Double value;

    public MwTextField(Double double1, Double step1, int j) {
        super();

        switch (j) {
            case 0:
                format = (PFORMAT);
                break;
            case 1:
                format = (IFORMAT);
                break;
            case 2:
                format = (DFORMAT);
                break;

            default:
                format = new DecimalFormat();

        }
        setText(format.format(double1));
        value = double1;
        step = step1;
        setEditable(false);

        // setSize(new Dimension(50,15));
        // setPreferredSize(getSize());
        addMouseMotionListener(this);
        addMouseListener(this);
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        this.setEditable(false);
        try {
            Double v = Double.valueOf(this.getText());
            if (v < 0) {
                v = (double) 0;
            }

            this.setText(format.format(v));
        } catch (final Exception ex) {
            this.setText(previousValue);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent ev) {

        final int newX = ev.getX();

        updateValue((newX - previousX));
        previousX = newX;

        ev.consume();
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        this.setEditable(false);
    }

    @Override
    public void mouseMoved(MouseEvent ev) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        previousX = e.getX();
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        previousValue = this.getText();
        if (e.getClickCount() == 2) {

            this.setEditable(true);
        } else {
            this.setEditable(false);
        }
        e.consume();
    }

    private void updateValue(int y) {

        value = value + (y > 0 ? 1 : -1) * step;

        if (value < 0) {
            value = (double) 0;
        }

        this.setText(format.format(value));
    }

}
