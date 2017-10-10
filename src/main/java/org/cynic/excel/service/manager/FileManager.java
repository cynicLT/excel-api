package org.cynic.excel.service.manager;

import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;

import java.util.List;

public interface FileManager {
    /**
     * Read values for constraint
     *
     * @param items  items
     * @param source source file
     * @return values for constraint
     */
    List<String> readConstraintValues(List<DataItem> items, byte[] source);

    /**
     * Read data from source
     *
     * @param values reading values
     * @param source source file
     * @return readConstraintValues data
     */
    List<Pair<DataItem, List<String>>> readSourceData(List<RuleValues> values, byte[] source);

    /**
     * Paste readConstraintValues data into file
     *
     * @param readData    data
     * @param destination destination file
     * @return merged file
     */
    byte[] pasteReadData(List<Pair<DataItem, List<String>>> readData, byte[] destination);

}
