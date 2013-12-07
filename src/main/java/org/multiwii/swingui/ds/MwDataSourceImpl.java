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

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * A pojo implementation of a datasource for holding severals dataset of sensors
 * 
 * @author treym
 */
public class MwDataSourceImpl implements MwDataSource {
	private static final Logger LOGGER = Logger
			.getLogger(MwDataSourceImpl.class);

	// TODO impl factory
	// private MwDataSourceImpl(){}

	private Map<Class<? extends MwSensorClass>, TimeSeriesCollection> dataset = new Hashtable<Class<? extends MwSensorClass>, TimeSeriesCollection>();

	private final Map<Class<? extends MwSensorClass>, List<MwDataSourceListener>> listeners = new Hashtable<Class<? extends MwSensorClass>, List<MwDataSourceListener>>();
	private long maxItemAge = 2000;

	private final Map<Class<? extends MwSensorClass>, Map<String, TimeSeries>> sensors = new Hashtable<Class<? extends MwSensorClass>, Map<String, TimeSeries>>();

	// public int getMaxItemCount() {
	// return maxItemCount;
	// }

	// TODO set max ages counts for each dataset not all

	// public void setMaxItemCount(final int maxItemCount1) {
	// if (maxItemCount1 > 0) {
	// // this.maxItemCount = maxItemCount1;
	// for (Class<? extends MwSensorClass> sclass : sensors.keySet()) {
	// Hashtable<String, TimeSeries> series = sensors.get(sclass);
	// for (String sensorName : series.keySet()) {
	// series.get(sensorName).setMaximumItemCount(maxItemCount);
	// }
	// }
	// }
	// }

	@Override
	public void addListener(final Class<? extends MwSensorClass> sensorClass,
			final MwDataSourceListener newListener) {
		if (sensorClass != null && newListener != null) {
			List<MwDataSourceListener> listenersl = listeners.get(sensorClass);
			if (listenersl == null) {
				listenersl = new ArrayList<MwDataSourceListener>();
				listeners.put(sensorClass, listenersl);
			}
			listenersl.add(newListener);
		}
	}

	/**
	 * Creates a dataset.
	 * 
	 * @return the dataset.
	 */

	@Override
	public final XYDataset getDataSet(
			final Class<? extends MwSensorClass> sensorClass) {

		if (dataset == null) {
			dataset = new Hashtable<Class<? extends MwSensorClass>, TimeSeriesCollection>();
			// dataset.setDomainIsPointsInTime(true);
		}
		if (sensorClass == null) {
			return new TimeSeriesCollection();
		}

		TimeSeriesCollection ts = dataset.get(sensorClass);
		if (ts == null) {
			ts = new TimeSeriesCollection();
			dataset.put(sensorClass, ts);
		}

		Map<String, TimeSeries> s = sensors.get(sensorClass);
		if (s == null) {
			s = new Hashtable<String, TimeSeries>();
			sensors.put(sensorClass, s);

		}
		for (final String sensorName : s.keySet()) {
			ts.addSeries(s.get(sensorName));

		}

		return dataset.get(sensorClass);

	}

	public final long getMaxItemAge() {
		return maxItemAge;
	}

	// public final XYDataset getDataset() {
	// if (dataset == null) {
	// return getLatestDataset();
	// }
	// return dataset;
	// }

	@Override
	public final void notifyListener(
			final Class<? extends MwSensorClass> sensorClass,
			final String name, final Double value) {
		if (sensorClass != null) {
			final List<MwDataSourceListener> listenersl = listeners
					.get(sensorClass);
			for (final MwDataSourceListener mwDataSourceListener : listenersl) {
				mwDataSourceListener.readNewValue(sensorClass, name, value);

			}

		}
	}

	@Override
	public final boolean put(final Date date, final String sensorName,
			final Double value, final Class<? extends MwSensorClass> sensorClass) {

		if (sensorName == null || sensorName.length() == 0) {
			return false;
		}

		if (sensorClass != null) {
			notifyListener(sensorClass, sensorName, value);
		}
		Map<String, TimeSeries> s = sensors.get(sensorClass);
		if (s == null) {
			s = new Hashtable<String, TimeSeries>();
			sensors.put(sensorClass, s);
		}
		TimeSeriesCollection ts = dataset.get(sensorClass);
		if (ts == null) {
			ts = new TimeSeriesCollection();
			dataset.put(sensorClass, ts);

		}

		TimeSeries timeserie = s.get(sensorName);

		if (timeserie == null) {
			timeserie = new TimeSeries(sensorName);
			// timeserie.setMaximumItemCount(maxItemCount);
			timeserie.setMaximumItemAge(maxItemAge);

			s.put(sensorName, timeserie);
			ts.addSeries(s.get(sensorName));

		}

		try {
			// if the refresh rate is high , we may have multiple answer within
			// the same millis
			timeserie.addOrUpdate(new Millisecond(date), value);
		} catch (final Exception e) {
			LOGGER.error(e.getMessage());
		}
		return true;

	}

	@Override
	public final boolean removeListener(
			final Class<? extends MwSensorClass> sensorClass,
			final MwDataSourceListener deadListener) {
		if (sensorClass != null && deadListener != null) {

			final List<MwDataSourceListener> listenersl = listeners
					.get(sensorClass);
			if (listenersl != null) {

				return listenersl.remove(deadListener);

			}

		}
		return false;
	}

	public final void setMaxItemAge(final int maxItemAge1) {
		if (maxItemAge1 > 0) {
			this.maxItemAge = maxItemAge1;
			for (final Class<? extends MwSensorClass> sclass : sensors.keySet()) {
				final Map<String, TimeSeries> series = sensors.get(sclass);
				for (final String sensorName : series.keySet()) {
					series.get(sensorName).setMaximumItemAge(maxItemAge);
				}
			}
		}

	}

}
