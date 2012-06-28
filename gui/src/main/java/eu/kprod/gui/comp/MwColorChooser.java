package eu.kprod.gui.comp;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.kprod.gui.MwGuiFrame;

public final class MwColorChooser extends MwJFrame implements ChangeListener {
    /**
     * 
     */
    private static final long serialVersionUID = 6206134450489506329L;
    private static JColorChooser chooser;
    private static MwColorChooser instance;
    private static int index=-1;
  
    private MwColorChooser() {
        super();

        MwJPanel pane = new MwJPanel();
        pane.setLayout(new BorderLayout());
        
        chooser = new JColorChooser();
        chooser.getSelectionModel().addChangeListener(this);
        chooser.setPreviewPanel(new JPanel());
        pane.add(chooser);
        getContentPane().add(pane);
        pack();
    }

    static void getInstance(int index1, Color c) {
        if (instance == null) {
            instance = new MwColorChooser();
        }
        index = index1 ;
        chooser.setColor(c);
        instance.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (index>=0){
            StyleColor.setColor(index,chooser.getColor());
            MwGuiFrame.getChartPanel().setVisible(index, true);
            MwGuiFrame.getChartCheckBoxPanel().refreshBox(index, chooser.getColor());
        }
        
    }
}