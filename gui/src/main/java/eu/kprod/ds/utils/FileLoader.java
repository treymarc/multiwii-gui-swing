/*  This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program;
 */

package eu.kprod.ds.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.kprod.ds.DSLoadable;
import eu.kprod.ds.MwDataSource;



/**
 * load a DataSource from a file.
 * @author treym
 *
 */
public class FileLoader implements DSLoadable {

    /**
     *
     */
    public FileLoader() {

    }

    @Override
    public final MwDataSource getDataSourceContent(final String s) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * charge un fichier ligne par ligne
     *
     * @param filePath
     *            le chemin du ficher à lire
     * @return le contenu du fichier,une liste vide pour une fichier vide, null
     *         en cas d'erreur
     * @throws IOException
     */
    public final List<String> getFileContent(final String filePath)
            throws IOException {

        final List<String> content = new ArrayList<String>();
        try {
            final BufferedReader buff = new BufferedReader(new FileReader(filePath));

            try {
                String line;
                while ((line = buff.readLine()) != null) {
                    content.add(line);
                }
            } finally {
                buff.close();
            }
        } catch (final IOException ioe) {
            throw ioe;
        }
        return content;
    }
}
