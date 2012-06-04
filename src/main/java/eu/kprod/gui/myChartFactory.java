package eu.kprod.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;

public class myChartFactory {

  
  public static JFreeChart createChart(myDataSource ds) {

//    final XYDataset dataset
    
    final JFreeChart chart = 
        ChartFactory.createTimeSeriesChart(
                                          null,
                                           null, 
                                           null,
                                           ds.getLatestDataset(),
                                           true,
                                           true,
                                           false
            );


    chart.setBackgroundPaint(Color.white);


    final XYPlot plot = chart.getXYPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    
    plot.setDomainCrosshairVisible(true);
    plot.setRangeCrosshairVisible(true);

//    final XYItemRenderer renderer = plot.getRenderer();
//    if (renderer instanceof XYItemRenderer) {
//      final XYItemRenderer rr = (XYItemRenderer) renderer;
//
////      rr.setBaseItemLabelsVisible(true );
////      rr.setItemLabelsVisible(true);
//      
//    }

    final DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("mm''ss''''SSS"));

    return chart;

  }



}
