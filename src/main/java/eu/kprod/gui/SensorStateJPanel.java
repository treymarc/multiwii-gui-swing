package eu.kprod.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SensorStateJPanel extends JPanel {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private JLabel lblTemp1;
  private JLabel lblHydro1;
 


  public SensorStateJPanel(){
    
    lblTemp1 = new JLabel( "Temp : ");
    lblHydro1 = new JLabel( "Hydro : ");
    

    this.add(lblTemp1,BorderLayout.LINE_START);
    this.add(lblHydro1,BorderLayout.LINE_START);
    

  }

  public void SetHydro(double value) {
    // TODO Auto-generated method stub
    lblHydro1.setText("Hydro : "+value);
  
  }

  public void SetTemp(double value) {
    // TODO Auto-generated method stub
    lblTemp1.setText("Temp : "+value);
 
  }
  

}
