package eu.kprod.gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import eu.kprod.ds.MwDataSourceListener;

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
      if(!isMouseWheelEnabled()){
        super.setMouseWheelEnabled(true);
        super.setDomainZoomable(true);
        super.setRangeZoomable(true);
      }
        
    }
    
}
