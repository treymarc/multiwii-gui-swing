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
 


    public Color getColor() {
        return color;
    }

    public void setColor(Color color1) {
        this.color = color1;
        setBackground(color);
    }

    public MwJButtonColorChooser( final int index, String name, Color color1) {
        // TODO Auto-generated constructor stub
        super("", name + " color");
        super.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MwColorChooser.getInstance(index,color);
            }
        });
        setBackground(color1);

        this.color = color1;
    }



}
