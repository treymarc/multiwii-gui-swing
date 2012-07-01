package eu.kprod.ds;

/**
 * 
 * 
 * @author treym
 * 
 */
public interface MwDataSourceListener {

    void readNewValue(final Integer string, final int i);

    void readNewValue(final String name, final Double value);

    void resetAllValues();
}
