package eu.kprod.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;

import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.ds.MwSensorClass;

public final class MwChartFactory {

    private MwChartFactory() {

    }
    
    public static ChartPanel createChart(final MwDataSource ds,Class<? extends MwSensorClass> sclass) {
        final JFreeChart chart;
        // final XYDataset dataset
       
        chart = ChartFactory.createTimeSeriesChart(null, null,
                    null, ds.getLatestDataset(sclass), true, true, false);
        

        chart.setBackgroundPaint(Color.white);
        
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        // final XYItemRenderer renderer = plot.getRenderer();
        // if (renderer instanceof XYItemRenderer) {
        // final XYItemRenderer rr = (XYItemRenderer) renderer;
        //
        // // rr.setBaseItemLabelsVisible(true );
        // // rr.setItemLabelsVisible(true);
        //
        // }

        final DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("mm''ss''''SSS"));

        MwChartPanel chartPanel = new MwChartPanel(chart);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        ds.addListener(sclass, (MwDataSourceListener)chartPanel);
        
        return chartPanel;

    }

}
