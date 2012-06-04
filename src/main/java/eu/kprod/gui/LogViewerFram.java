package eu.kprod.gui;

import java.io.IOException;

import javax.swing.JFrame;


import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import eu.kprod.utils.logLoader;

public class LogViewerFram extends JFrame{

  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private static final Logger LogViewerFram = Logger.getLogger(LogViewerFram.class);

  
  public LogViewerFram(String name) {
    // TODO Auto-generated constructor stub
    super(name);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    myDataSource ds;
    try {
      ds = logLoader.getDataSourceContent(name);
    } catch (IOException e) {
      LogViewerFram.error("Can not open log file : "+name);
      e.printStackTrace();
      ds = new myDataSource();
    }
    ChartPanel chartTrendPanel = new ChartPanel(myChartFactory.createChart(ds));

    getContentPane().add(chartTrendPanel);
    setPreferredSize(new java.awt.Dimension(500, 270));
    setVisible(true);
    repaint();
  }

}
