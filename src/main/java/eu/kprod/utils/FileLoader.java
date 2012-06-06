package eu.kprod.utils;

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
public class FileLoader implements DSLoadable{

    /**
     *
     */
    public FileLoader() {
        
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

        List<String> content = new ArrayList<String>();
        try {
            BufferedReader buff = new BufferedReader(new FileReader(filePath));

            try {
                String line;
                while ((line = buff.readLine()) != null) {
                    content.add(line);
                }
            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            throw ioe;
        }
        return content;
    }

    public MwDataSource getDataSourceContent(String s) {
        // TODO Auto-generated method stub
        return null;
    }
}
