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
package eu.kprod.gui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import eu.kprod.gui.comp.StyleColor;

public final class MwChartFactory {

    public static MwChartPanel createChart(final XYDataset xyDataset) {
        final JFreeChart chart;

        chart = ChartFactory.createTimeSeriesChart(null, null, null, xyDataset,
                false, true, true);

        chart.setBackgroundPaint(StyleColor.BACKGROUND_COLOR);
        chart.setBorderVisible(false);
        final XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(StyleColor.BACKGROUND_COLOR);

        plot.setDomainGridlinePaint(StyleColor.FORGROUND_COLOR);
        plot.setRangeGridlinePaint(StyleColor.FORGROUND_COLOR);
        plot.setDomainGridlinesVisible(false);
        plot.setDomainCrosshairPaint(StyleColor.FORGROUND_COLOR);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        final DateAxis axis = (DateAxis) plot.getDomainAxis();
        // axis.setDateFormatOverride(new SimpleDateFormat("mm''ss''''SSS"));
        axis.setAxisLineVisible(false);
        axis.setTickLabelsVisible(false);
        axis.setTickLabelPaint(StyleColor.FORGROUND_COLOR);

        // force integer display
        final ValueAxis va = plot.getRangeAxis();
        va.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        va.setLabelPaint(StyleColor.FORGROUND_COLOR);
        va.setAxisLinePaint(StyleColor.FORGROUND_COLOR);
        va.setTickLabelPaint(StyleColor.FORGROUND_COLOR);
        //        va.setRange(-280,280);
        //        va.setFixedAutoRange(560);
        //        va.setLowerBound(-280);
        //        va.setUpperBound(280);
        //        va.setAutoRange(false);
        va.setRangeWithMargins(-280, 280);

        final MwChartPanel chartPanel = new MwChartPanel(chart);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        return chartPanel;

    }

    private MwChartFactory() {

    }

}
