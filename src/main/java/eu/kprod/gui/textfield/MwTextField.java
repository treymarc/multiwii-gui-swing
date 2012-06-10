package eu.kprod.gui.textfield;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JTextField;

public class MwTextField extends JTextField implements MouseListener,MouseMotionListener {

    private static final NumberFormat formatP =  new DecimalFormat("0.00");
    private static final NumberFormat formatI =  new DecimalFormat("0.0000");
    private static final NumberFormat formatD =  new DecimalFormat("0.00");
  
    private  NumberFormat format;
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Double value;
    private Double step;
    private int previousX;
   
    public MwTextField(Double double1, Double step1, int j){
        super();
        switch (j) {
            case 0:
                format= (formatP);
                break;
            case 1:
                format= (formatI);
                break;
            case 2:
                format= (formatD);
                break;

            default:
                throw new RuntimeException();
               
        }
        setText(format.format(double1));
       value =double1;
       step = step1;
    setEditable(false);
    
    setSize(new Dimension(50,15));
    setPreferredSize(getSize());
    addMouseMotionListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent ev) {
        
        int newX = ev.getX();
          
        updateValue((newX - previousX));
        previousX = newX;
        
        ev.consume();
    }

    private void updateValue(int y) {

        value = value + (y>0?1 :-1)*step;

        if (value<0){
            value=(double) 0;
        }
    
        this.setText(format.format(value));
    }

    @Override
    public void mouseMoved(MouseEvent ev) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        previousX = e.getX();
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    
}
