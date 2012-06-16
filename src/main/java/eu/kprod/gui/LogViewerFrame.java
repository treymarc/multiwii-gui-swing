package eu.kprod.gui;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import eu.kprod.ds.DSLoadableException;
import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwDataSourceImpl;
import eu.kprod.ds.MwSensorClass;
import eu.kprod.ds.utils.LogLoader;
import eu.kprod.gui.chart.MwChartFactory;
import eu.kprod.gui.chart.MwChartPanel;

public class LogViewerFrame extends JFrame {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogViewerFrame.class);
    private MwDataSource refDs;
    private Class<? extends MwSensorClass> refsclass;
    private MwChartPanel chartTrendPanel;

    private final void frameSetDefaultPosition() {
        // TODO , get last frame position
        setPreferredSize(new java.awt.Dimension(500, 270));
        setSize(new java.awt.Dimension(500, 270));
        setVisible(true);
        pack();
    }

    public LogViewerFrame(String name, MwDataSource ds,
            Class<? extends MwSensorClass> sclass) {
        super(name);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        refDs = ds;
        refsclass = sclass;
        chartTrendPanel = MwChartFactory.createChart(ds.getDataSet(sclass));
        ds.addListener(sclass, chartTrendPanel);
        getContentPane().add(chartTrendPanel);
        frameSetDefaultPosition();
    }

    public LogViewerFrame(String name, MwDataSource mwDataSource) {
        super(name);
        // when loading a file, we want to dipose the frame after usage
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        MwDataSource ds;
        try {
            ds = new LogLoader().getDataSourceContent(name);
        } catch (DSLoadableException e) {
            LOGGER.error("Can not open log file : " + name);
            e.printStackTrace();
            // TODO get datasource impl
            ds = new MwDataSourceImpl();
        }
        refDs = ds;
        chartTrendPanel = MwChartFactory.createChart(ds
                .getDataSet(MwSensorClass.class));
        chartTrendPanel.setPreferredSize(new java.awt.Dimension(500, 270));
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

}
