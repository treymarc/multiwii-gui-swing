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

    /**
     *
     */
    public SensorStateJPanel() {

        lblTemp1 = new JLabel("Temp : ");
        lblHydro1 = new JLabel("Hydro : ");

        this.add(lblTemp1, BorderLayout.LINE_START);
        this.add(lblHydro1, BorderLayout.LINE_START);

    }

    /**
     * 
     * @param value
     */
    public void setHydro(final double value) {
        // TODO Auto-generated method stub
        lblHydro1.setText("Hydro : " + value);

    }

    /**
     * 
     * @param value
     */
    public void setTemp(final double value) {
        // TODO Auto-generated method stub
        lblTemp1.setText("Temp : " + value);

    }

}
