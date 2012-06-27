package eu.kprod.ds;

/**
 * 
 * 
 * @author treym
 * 
 */
public interface MwDataSourceListener {

    public void readNewValue(final String name, final Double value);

    public void resetAllValues();

    public void readNewValue(final Integer string, final int i);
}
