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

 

    /**
     * ask for a specific dataset of sensor
     * @param sclass SensorClass of the dataset
     * @return the dataset
     */
    public  XYDataset getDataSet( Class<? extends MwSensorClass> sclass) ;

    /**
     * add a value to a dataset
     * 
     * @param date date of the value
     * @param sensorName name of the value
     * @param value the value
     * @param sclass the sensorclass of the value
     * @return
     */
    public  boolean put(final Date date, final String sensorName, final Double value, Class<? extends MwSensorClass> sclass);

    public void addListener(Class<? extends MwSensorClass> sensor,  MwDataSourceListener listener );

    boolean removeListener(Class<? extends MwSensorClass> sensorClass,
            MwDataSourceListener newListener);

    /**
     *  some overhead here ,worth extending http://www.jfree.org/jfreechart/api/gjdoc/org/jfree/data/general/DatasetChangeEvent.html
     * @param sensorClass
     */
    void notifyListener(MwSensorClass sensorClass);
}
