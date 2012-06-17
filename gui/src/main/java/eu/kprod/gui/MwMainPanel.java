package eu.kprod.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JTabbedPane;

import eu.kprod.gui.comp.MwJPanel;
import eu.kprod.gui.comp.MwJTabbedPane;

public class MwMainPanel extends MwJPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwMainPanel(Component realTimePanel, Component settingsPanel) {
        // take all the place
        super(new GridLayout(1, 1));

        JTabbedPane tabbedPane = new MwJTabbedPane();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("RealTime Data", null, realTimePanel, null);
        tabbedPane.addTab("Settings", null, settingsPanel, null);
      
        this.add(tabbedPane);

    }

}