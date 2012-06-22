package eu.kprod.ds;

/**
 * 
 * 
 * @author treym
 * 
 */
public interface MwDataSourceListener {

    public void readNewValue(String name, Double value);
    public void resetAllValues();
}
