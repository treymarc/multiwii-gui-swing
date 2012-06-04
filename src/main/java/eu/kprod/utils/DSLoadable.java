package eu.kprod.utils;

import eu.kprod.gui.MwiDataSource;

public interface DSLoadable {
    public MwiDataSource getDataSourceContent(String s) throws DSLoadableException;
}
