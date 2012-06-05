package eu.kprod.gui;

import java.util.Date;
import java.util.Hashtable;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class MwiDataSource {

    private Hashtable<String, TimeSeries> sensors = new Hashtable<String, TimeSeries>();
    private TimeSeriesCollection dataset;

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
            // TODO get user settings
            // timeserie.setMaximumItemCount(300);
            timeserie.setMaximumItemAge(5000);
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
