package eu.kprod.gui.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.gui.comp.StyleColor;

public class MwChartPanel extends ChartPanel implements MwDataSourceListener {

    public MwChartPanel(JFreeChart chart) {
        super(chart);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void readNewValue(String name, Double value) {
        if (!isMouseWheelEnabled()) {
            super.setMouseWheelEnabled(true);
            super.setDomainZoomable(true);
            super.setRangeZoomable(true);
        }

    }

    public void setVisible(int l, boolean b) {
        XYItemRenderer renderer = super.getChart().getXYPlot().getRenderer();
        for (int i = 0; i < super.getChart().getXYPlot().getDataset()
                .getSeriesCount(); i++) {
            super.getChart().getXYPlot().getSeriesCount();
        }

        renderer.setSeriesVisible(l, b);
        if (b) {
            renderer.setSeriesPaint(l, StyleColor.getColor(l));
        }
    }
}
