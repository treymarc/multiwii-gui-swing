package eu.kprod.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;

import eu.kprod.ds.MwDataSource;
import eu.kprod.ds.MwSensorClassIMU;

public final class MwChartFactory {

    private MwChartFactory() {

    }
    
    public static JFreeChart createChart(final MwDataSource ds) {

        // final XYDataset dataset

        final JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null,
                null, ds.getLatestDataset(MwSensorClassIMU.class), true, true, false);

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

        return chart;

    }

}
