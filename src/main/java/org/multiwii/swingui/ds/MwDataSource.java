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
package org.multiwii.swingui.ds;

import java.util.Collection;
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
     * get all sensors class in this dataset
     * 
     * @param sclass
     *            SensorClass of the dataset
     * @return the dataset
     */
    Collection<Class<? extends MwSensorClass>>getSensorsClass();

   
    /**
     * get all sensors of class sclass 
     * 
     * @param sclass
     *            SensorClass of the dataset
     * @return the dataset
     */
   Collection<String> getSensorsName(Class<? extends MwSensorClass> sensorClass);

    
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

	int getIndex(String l);

}
