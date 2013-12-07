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
package org.multiwii.swingui.ds.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.multiwii.swingui.ds.MwDataSource;
import org.multiwii.swingui.ds.MwDataSourceImpl;
import org.multiwii.swingui.ds.MwSensorClass;

/**
 * load a DataSource from a formated Log file.
 * 
 * @author treym
 * 
 */
public class SensorsLogLoader implements MwDataSourceLoader {
	/*
	 * 2013-11-16
	 * 06:50:40,BACC3335012840A14BA5,ACC,33,channel,3,temp,28.0,hum,14,bat,5
	 */

	public SensorsLogLoader() {
	}

	/**
	 * charge un fichier ligne par ligne
	 * 
	 * @param filePath
	 *            le chemin du ficher à lire
	 * @return le contenu du fichier,une liste vide pour une fichier vide, null
	 *         en cas d'erreur
	 * @throws MWDataSourceLoaderException
	 */

	public static MwDataSource getDataSourceContent(MwDataSource content,
			String filePath) throws MWDataSourceLoaderException {

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			final BufferedReader buff = new BufferedReader(new FileReader(
					filePath));

			try {
				String line;
				while ((line = buff.readLine()) != null) {
					try {
						final String[] content1 = line.split(",", 15);
						;

						content.put(sdf.parse(content1[0]), content1[6]+ content1[5], Double.valueOf(content1[7]),
								MwSensorClass.class);
						content.put(sdf.parse(content1[0]), content1[8]+ content1[5], Double.valueOf(content1[9]),
								MwSensorClass.class);
						
					} catch (final Exception e) {
						e.printStackTrace();
						// failed to load
						break;

					}
				}
			} finally {
				buff.close();
			}
		} catch (final IOException ioe) {
			throw new MWDataSourceLoaderException(ioe);
		}
		return content;
	}


	public MwDataSource getDataSourceContent(String source)
			throws MWDataSourceLoaderException {
		// TODO Auto-generated method stub
		return getDataSourceContent(new MwDataSourceImpl(), source);
	}
}
