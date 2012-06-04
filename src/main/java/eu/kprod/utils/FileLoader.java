package eu.kprod.utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

  /**
   * charge un fichier ligne par ligne
   * 
   * @param filePath le chemin du ficher à lire
   * @return le contenu du fichier,une liste vide pour une fichier vide, null en cas d'erreur 
   * @throws IOException 
   */
  public static List<String> getFileContent(String filePath) throws IOException{

    
    List<String> content = new ArrayList<String>(); 
    try{
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
}