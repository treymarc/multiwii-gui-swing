package eu.kprod.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MwMainPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwMainPanel(Component realTimePanel, Component settingsPanel) {
        // take all the place
        super(new GridLayout(1, 1));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("RealTime Data", null, realTimePanel, null);
        tabbedPane.addTab("Settings", null, settingsPanel, null);

        this.add(tabbedPane);

    }

}