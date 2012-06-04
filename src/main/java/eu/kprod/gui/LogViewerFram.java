package eu.kprod.gui;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import eu.kprod.utils.DSLoadableException;
import eu.kprod.utils.LogLoader;


public class LogViewerFram extends JFrame {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger
            .getLogger(LogViewerFram.class);

    public LogViewerFram(String name) {
        // TODO Auto-generated constructor stub
        super(name);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        MwiDataSource ds;
        try {
            ds = new LogLoader().getDataSourceContent(name);
        } catch (DSLoadableException e) {
            LOGGER.error("Can not open log file : " + name);
            e.printStackTrace();
            ds = new MwiDataSource();
        }
        ChartPanel chartTrendPanel = new ChartPanel(
                MwiChartFactory.createChart(ds));

        getContentPane().add(chartTrendPanel);
        setPreferredSize(new java.awt.Dimension(500, 270));
        setVisible(true);
        repaint();
    }

}
