package eu.kprod.utils;

import eu.kprod.gui.MwDataSource;

/**
 * DataSource that can be load from a source
 * @author treym
 *
 */
public interface DSLoadable {
    public MwDataSource getDataSourceContent(String source) throws DSLoadableException;
}
