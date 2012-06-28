package eu.kprod.ds;

/**
 * 
 * 
 * @author treym
 * 
 */
public interface MwDataSourceListener {

    void readNewValue(final String name, final Double value);

    void resetAllValues();

    void readNewValue(final Integer string, final int i);
}
