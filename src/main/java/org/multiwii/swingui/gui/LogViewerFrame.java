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
package org.multiwii.swingui.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.multiwii.swingui.ds.MwDataSource;
import org.multiwii.swingui.ds.MwDataSourceImpl;
import org.multiwii.swingui.ds.MwSensorClass;
import org.multiwii.swingui.ds.utils.LogLoader;
import org.multiwii.swingui.ds.utils.MWDataSourceLoaderException;
import org.multiwii.swingui.gui.chart.MwChartFactory;
import org.multiwii.swingui.gui.chart.MwChartPanel;

public class LogViewerFrame extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(LogViewerFrame.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final MwChartPanel chartTrendPanel;
    private final MwDataSource refDs;
    private Class<? extends MwSensorClass> refsclass;

    public LogViewerFrame(final String name, final MwDataSource mwDataSource,MwConfiguration conf) {
        super(name);
        // when loading a file, we want to dipose the frame after usage
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        MwDataSource ds;
        try {
          
				ds = new LogLoader().getDataSourceContent(name);
		
        } catch (final MWDataSourceLoaderException e) {
            LOGGER.error("Can not open log file : " + name+"\n");
            ds = new MwDataSourceImpl();
        }
        refDs = ds;
        chartTrendPanel = MwChartFactory.createChart(conf, ds);
        chartTrendPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        getContentPane().add(chartTrendPanel);
        frameSetDefaultPosition();
    }

//    public LogViewerFrame(final String name, final MwDataSource ds,
//            final Class<? extends MwSensorClass> sclass,MwConfiguration conf) {
//        super(name);
//        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//        refDs = ds;
//        refsclass = sclass;
//        chartTrendPanel = MwChartFactory.createChart(conf,ds);
//        ds.addListener(sclass, chartTrendPanel);
//        getContentPane().add(chartTrendPanel);
//        frameSetDefaultPosition();
//    }

    @Override
    public final void dispose() {
        if (refDs != null) {
            refDs.removeListener(refsclass, chartTrendPanel);
        }
        super.dispose();
    }

    private void frameSetDefaultPosition() {
        setPreferredSize(new java.awt.Dimension(500, 270));
        setSize(new java.awt.Dimension(500, 270));
        setVisible(true);
        pack();
    }

}
