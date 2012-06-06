package eu.kprod.gui;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import eu.kprod.ds.DSLoadableException;
import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwDataSourceImpl;

import eu.kprod.utils.LogLoader;


public class LogViewerFrame extends JFrame {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger
            .getLogger(LogViewerFrame.class);

    public LogViewerFrame(String name) {
        // TODO Auto-generated constructor stub
        super(name);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        MwDataSource ds;
        try {
            ds = new LogLoader().getDataSourceContent(name);
        } catch (DSLoadableException e) {
            LOGGER.error("Can not open log file : " + name);
            e.printStackTrace();
            //TODO get datasource impl
            ds = new MwDataSourceImpl();
        }
        ChartPanel chartTrendPanel = new ChartPanel(
                MwChartFactory.createChart(ds));

        getContentPane().add(chartTrendPanel);
        setPreferredSize(new java.awt.Dimension(500, 270));
        setVisible(true);
        repaint();
    }

}
