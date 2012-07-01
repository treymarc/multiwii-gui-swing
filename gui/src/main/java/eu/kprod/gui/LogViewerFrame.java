package eu.kprod.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import eu.kprod.ds.DSLoadableException;
import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwDataSourceImpl;
import eu.kprod.ds.MwSensorClass;
import eu.kprod.ds.utils.LogLoader;
import eu.kprod.gui.chart.MwChartFactory;
import eu.kprod.gui.chart.MwChartPanel;

public class LogViewerFrame extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(LogViewerFrame.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final MwChartPanel chartTrendPanel;
    private final MwDataSource refDs;
    private Class<? extends MwSensorClass> refsclass;

    public LogViewerFrame(final String name, final MwDataSource mwDataSource) {
        super(name);
        // when loading a file, we want to dipose the frame after usage
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        MwDataSource ds;
        try {
            ds = new LogLoader().getDataSourceContent(name);
        } catch (final DSLoadableException e) {
            LOGGER.error("Can not open log file : " + name);
            ds = new MwDataSourceImpl();
        }
        refDs = ds;
        chartTrendPanel = MwChartFactory.createChart(ds
                .getDataSet(MwSensorClass.class));
        chartTrendPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        getContentPane().add(chartTrendPanel);
        frameSetDefaultPosition();
    }

    public LogViewerFrame(final String name, final MwDataSource ds,final 
            Class<? extends MwSensorClass> sclass) {
        super(name);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        refDs = ds;
        refsclass = sclass;
        chartTrendPanel = MwChartFactory.createChart(ds.getDataSet(sclass));
        ds.addListener(sclass, chartTrendPanel);
        getContentPane().add(chartTrendPanel);
        frameSetDefaultPosition();
    }

    @Override
    public void dispose() {
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
