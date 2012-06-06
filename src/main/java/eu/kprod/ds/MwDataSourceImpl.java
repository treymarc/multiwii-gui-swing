package eu.kprod.ds;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

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
    
    private Hashtable<MwSensorClass, List<MwDataSourceListener>> listeners = new Hashtable< MwSensorClass,  List<MwDataSourceListener>>();
    
    private Hashtable<Class< ? extends MwSensorClass> , Hashtable<String, TimeSeries>> sensors = new Hashtable<Class<? extends MwSensorClass> , Hashtable<String, TimeSeries>>();
    private TimeSeriesCollection dataset;
    
    private long maxItemAge = 5000;
    private int maxItemCount = 4000;
    
    public int getMaxItemCount() {
        return maxItemCount;
    }

    public void setMaxItemCount(final int maxItemCount1) {       
        if (maxItemCount1>0){
            this.maxItemCount = maxItemCount1;
            for (Class<? extends MwSensorClass> sclass : sensors.keySet()) {
                Hashtable<String, TimeSeries> series = sensors.get(sclass);
                for (String sensorName : series.keySet()) {
                    series.get(sensorName).setMaximumItemCount(maxItemCount);
                }
            } 
        }

    }

    public long getMaxItemAge() {
        return maxItemAge;
    }


    
    public void setMaxItemAge(final int maxItemAge1) {       
        if (maxItemAge1>0){
            this.maxItemAge = maxItemAge1;
            for (Class< ? extends MwSensorClass> sclass : sensors.keySet()) {
                Hashtable<String, TimeSeries> series = sensors.get(sclass);
                for (String sensorName : series.keySet()) {
                    series.get(sensorName).setMaximumItemAge(maxItemAge);
                }
            } 
        }

    }

    /**
     * Creates a dataset.
     * 
     * @return the dataset.
     */

    public final XYDataset getLatestDataset(Class<? extends MwSensorClass> sensorClass) {
        
        if( sensorClass == null){
            return new TimeSeriesCollection();
        }
        

        
        if (dataset == null) {

            dataset = new TimeSeriesCollection();
            Hashtable<String, TimeSeries> s = sensors.get(sensorClass);
            if (s==null){
                s= new Hashtable<String, TimeSeries> ();
                sensors.put(sensorClass, s);
            }
            for (String sensorName : s.keySet()) {
                dataset.addSeries(sensors.get(sensorClass).get(sensorName));
            }

            // dataset.setDomainIsPointsInTime(true);
        }
        return dataset;

    }

//    public final XYDataset getDataset() {
//        if (dataset == null) {
//            return getLatestDataset();
//        }
//        return dataset;
//    }

    public final boolean put(final Date date, final String sensorName, final Double value,Class<? extends MwSensorClass> sensorClass) {

        if (sensorName == null || sensorName.length() == 0) {
            return false;
        }

        if (sensorClass!=null){
            List<MwDataSourceListener> lists = listeners.get(sensorClass);
            if (lists != null){
                for (MwDataSourceListener mwDataSourceListener : lists) {
                    mwDataSourceListener.readNewValue(sensorName, value);
                }

            } ;
        }
        Hashtable<String, TimeSeries> s = sensors.get(sensorClass);
        if (s==null){
            s=new Hashtable<String, TimeSeries>();
            sensors.put(sensorClass, s);
        }
        TimeSeries timeserie = s.get(sensorName);

        if (timeserie == null) {
            timeserie = new TimeSeries(sensorName, Millisecond.class);
            timeserie.setMaximumItemCount(maxItemCount);
            timeserie.setMaximumItemAge(maxItemAge );
            sensors.get(sensorClass).put(sensorName, timeserie);
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



    @Override
    public void addListener(MwSensorClass sensorClass, MwDataSourceListener newListener) {
        if (sensorClass !=null && newListener != null ){
            List<MwDataSourceListener> listenersl = listeners.get(sensorClass);
            if (listenersl == null){
                listenersl = new ArrayList<MwDataSourceListener>();
                listeners.put(sensorClass, listenersl);
            }
            listenersl.add(newListener);
        }
    }
    

    @Override
    public boolean removeListener(MwSensorClass sensorClass, MwDataSourceListener deadListener) {
        if (sensorClass !=null && deadListener != null ){
           
            List<MwDataSourceListener> listenersl = listeners.get(sensorClass);
            if (listenersl != null){
                
                return listenersl.remove(deadListener);
               
            }
            
        }
        return false;
    }



}
