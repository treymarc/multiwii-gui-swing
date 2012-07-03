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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.gui.comp.StyleColor;

public class MwChartPanel extends ChartPanel implements MwDataSourceListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MwChartPanel(final JFreeChart chart) {
        super(chart);
        // TODO Auto-generated constructor stub
    }

    @Override
    public final void readNewValue(final Integer string, final int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public final void readNewValue(final String name, final Double value) {
        if (!isMouseWheelEnabled()) {
            super.setMouseWheelEnabled(true);
            super.setDomainZoomable(true);
            super.setRangeZoomable(true);
        }

    }

    @Override
    public void resetAllValues() {
        // TODO Auto-generated method stub
        // nothing todo (replace this from this is not possible)
    }

    public void setVisible(final int l, final boolean b) {
        final XYItemRenderer renderer = super.getChart().getXYPlot()
                .getRenderer();
        // for (int i = 0; i < super.getChart().getXYPlot().getDataset()
        // .getSeriesCount(); i++) {
        // super.getChart().getXYPlot().getSeriesCount();
        // }
        super.getChart().setAntiAlias(true);

        renderer.setSeriesVisible(l, b);
        if (b) {
            renderer.setSeriesPaint(l, StyleColor.getColor(l));
        }

    }
}
