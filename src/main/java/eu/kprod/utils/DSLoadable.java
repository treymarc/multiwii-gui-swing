package eu.kprod.utils;

import eu.kprod.gui.MwiDataSource;

/**
 * DataSource that can be load from a source
 * @author treym
 *
 */
public interface DSLoadable {
    public MwiDataSource getDataSourceContent(String source) throws DSLoadableException;
}
