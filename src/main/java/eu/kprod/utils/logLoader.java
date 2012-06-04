package eu.kprod.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import eu.kprod.gui.myDataSource;


public class logLoader extends FileLoader {
  
  
  /**
   * charge un fichier ligne par ligne
   * 
   * @param filePath le chemin du ficher à lire
   * @return le contenu du fichier,une liste vide pour une fichier vide, null en cas d'erreur 
   * @throws IOException 
   */
  public static  myDataSource getDataSourceContent(String filePath) throws IOException{

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:SS");

   
    
    myDataSource content = new myDataSource(); 
    
    try{
      BufferedReader buff = new BufferedReader(new FileReader(filePath));

      try {
        String line;
        while ((line = buff.readLine()) != null) {
          try{
          String[] content1 = parse(line);
          
//          System.err.println("content1[0] = "+content1[0]);
//          System.err.println("content1[1] = "+content1[1]);
          String date = filePath.substring(filePath.length()-10) +" "+line.substring(0,line.indexOf(":")+6);
//           System.err.println("date = "+date);
          content.put(sdf.parse(date),content1[0], Double.valueOf(content1[1]));
          }catch (Exception e) {
//            e.printStackTrace();
          }
        }
      } finally {
        buff.close();
      }
    } catch (IOException ioe) {
      throw ioe;
    }
    return content;
  }

  private static String[] parse(String line) {
//    System.err.println("line = "+line);
    String[] content = new String[3];
    // TODO Auto-generated method stub
    if (line.contains("SENSOR") && line.contains(":")){
      String s = line.substring(line.lastIndexOf('R')+1);
      int pos = s.lastIndexOf(':');
      content[0]=s.substring(0, pos-1);
      content[1]=s.substring(pos+1);
    
//      System.err.println(" -> content[0] = "+content[0]);
//      System.err.println(" -> content[1] = "+content[1]);
//      
    }
    
    
    return content  ;
  }
}
