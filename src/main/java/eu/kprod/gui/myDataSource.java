package eu.kprod.gui;

import java.util.Date;
import java.util.Hashtable;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class myDataSource {



	 private  Hashtable<String,TimeSeries> Sensors = new Hashtable<String,TimeSeries>(); 
  private  TimeSeriesCollection dataset;



  /**
   * Creates a dataset.
   *
   * @return the dataset.
   */


   public  XYDataset getLatestDataset() {
    if (dataset== null){

      dataset = new TimeSeriesCollection();

      for (String sensorName : Sensors.keySet()) {
        dataset.addSeries(Sensors.get(sensorName));
      }


//      dataset.setDomainIsPointsInTime(true);
    }
    return dataset;

  }

   public  XYDataset getDataset(){
    if (dataset == null){
      return getLatestDataset();
    }
    return dataset;
  }


   public  boolean put(Date date , String sensorName, Double value){

    if (sensorName == null || sensorName.length() == 0){
      return false;
    }

    TimeSeries timeserie = Sensors.get(sensorName);

    if(timeserie == null){
      timeserie = new TimeSeries(sensorName, Millisecond.class);
//      timeserie.setMaximumItemCount(300);
      timeserie.setMaximumItemAge(5000);
      Sensors.put(sensorName, timeserie);
      dataset.addSeries(timeserie);
    }

    timeserie.add(new Millisecond(date ), value);
    return true;


  }

}
