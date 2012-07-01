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

    void addListener(Class<? extends MwSensorClass> sensor,
            MwDataSourceListener listener);

    /**
     * ask for a specific dataset of sensor
     * 
     * @param sclass
     *            SensorClass of the dataset
     * @return the dataset
     */
    XYDataset getDataSet(Class<? extends MwSensorClass> sclass);

    /**
     * some overhead here ,worth extending
     * http://www.jfree.org/jfreechart/api/gjdoc
     * /org/jfree/data/general/DatasetChangeEvent.html
     * 
     * @param sensorClass
     */
    void notifyListener(Class<? extends MwSensorClass> sensorClass,
            String name, Double value);

    /**
     * add a value to a dataset
     * 
     * @param date
     *            date of the value
     * @param sensorName
     *            name of the value
     * @param value
     *            the value
     * @param sclass
     *            the sensorclass of the value
     * @return
     */
    boolean put(final Date d, final String sensorName,
            final Double value, Class<? extends MwSensorClass> sclass);

    boolean removeListener(Class<? extends MwSensorClass> sensorClass,
            MwDataSourceListener newListener);

}
