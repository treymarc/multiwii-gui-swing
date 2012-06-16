package eu.kprod.gui.comp;

import java.awt.Component;

import javax.swing.JSplitPane;

public class MwJSplitPane extends JSplitPane {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwJSplitPane(int horizontalSplit, Component hudPanel,
            Component realTimeChart) {
        super( horizontalSplit,  hudPanel,realTimeChart);

    }

}
