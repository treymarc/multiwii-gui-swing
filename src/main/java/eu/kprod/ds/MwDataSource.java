package eu.kprod.ds;

import java.util.Date;

import org.jfree.data.xy.XYDataset;

/**
 * XYDataset to track values over time
 * 
 * @author treym
 *
 */
public interface MwDataSource {

 

    public  XYDataset getLatestDataset() ;

    public  XYDataset getDataset();

    public  boolean put(final Date date, final String sensorName, final Double value);

}
