package eu.kprod.ds;

import java.util.Date;
import java.util.Hashtable;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * 
 * @author treym
 *
 */
public class MwDataSourceImpl implements MwDataSource {

    
    // TODO impl factory
//    private MwDataSourceImpl(){}
    
    private Hashtable<String, TimeSeries> sensors = new Hashtable<String, TimeSeries>();
    private TimeSeriesCollection dataset;
    
    private long maxItemAge = 5000;
    private int maxItemCount = 4000;
    
    public int getMaxItemCount() {
        return maxItemCount;
    }

    public void setMaxItemCount(final int maxItemCount1) {       
        if (maxItemCount1>0){
            this.maxItemCount = maxItemCount1;
            for (String sensorName : sensors.keySet()) {
                sensors.get(sensorName).setMaximumItemCount(maxItemCount);
            }
        }

    }

    public long getMaxItemAge() {
        return maxItemAge;
    }

    public void setMaxItemAge(final long maxItemAge1) {
        if (maxItemAge1>0){
            this.maxItemAge = maxItemAge1;
            for (String sensorName : sensors.keySet()) {
                sensors.get(sensorName).setMaximumItemAge(maxItemAge);
            }
        }

    }


    /**
     * Creates a dataset.
     * 
     * @return the dataset.
     */

    public final XYDataset getLatestDataset() {
        if (dataset == null) {

            dataset = new TimeSeriesCollection();

            for (String sensorName : sensors.keySet()) {
                dataset.addSeries(sensors.get(sensorName));
            }

            // dataset.setDomainIsPointsInTime(true);
        }
        return dataset;

    }

    public final XYDataset getDataset() {
        if (dataset == null) {
            return getLatestDataset();
        }
        return dataset;
    }

    public final boolean put(final Date date, final String sensorName, final Double value) {

        if (sensorName == null || sensorName.length() == 0) {
            return false;
        }

        TimeSeries timeserie = sensors.get(sensorName);

        if (timeserie == null) {
            timeserie = new TimeSeries(sensorName, Millisecond.class);
            timeserie.setMaximumItemCount(maxItemCount);
            timeserie.setMaximumItemAge(maxItemAge );
            sensors.put(sensorName, timeserie);
            dataset.addSeries(timeserie);
        }

        try {
            // if the refresh rate is high , we may have multiple answer within the same millis
            timeserie.addOrUpdate(new Millisecond(date), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

}
