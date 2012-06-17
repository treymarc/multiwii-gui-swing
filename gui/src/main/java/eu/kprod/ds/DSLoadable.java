package eu.kprod.ds;

/**
 * DataSource that can be load from a source
 *
 * @author treym
 *
 */
public interface DSLoadable {
    /**
     * return the data source from the path
     * @param source
     * @return the datasource
     * @throws DSLoadableException
     */
    MwDataSource getDataSourceContent(String source)
            throws DSLoadableException;
}
