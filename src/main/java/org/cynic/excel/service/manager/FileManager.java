package org.cynic.excel.service.manager;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FileManager {
    /**
     * Read values for constraint
     *
     * @param items  items
     * @param source source file
     * @return values for constraint
     */
    public abstract List<String> readConstraintValues(List<DataItem> items, byte[] source);

    /**
     * Read data from source
     *
     * @param values reading values
     * @param source source file
     * @return readConstraintValues data
     */
    public abstract List<Pair<DataItem, List<String>>> readSourceData(List<RuleValues> values, byte[] source);

    /**
     * Paste readConstraintValues data into file
     *
     * @param readData    data
     * @param destination destination file
     * @return merged file
     */
    public abstract byte[] pasteReadData(List<Pair<DataItem, List<String>>> readData, byte[] destination);

    List<Pair<DataItem, List<String>>> internalReadData(List<RuleValues> values, List<String[]> xslData) {
        return values.stream().map(ruleValues -> {
            DataItem dataItem = ruleValues.getStart();
            Validate.isTrue(xslData.size() > dataItem.getRow(), String.format("Bad source data column row %d", dataItem.getRow()));

            return Pair.of(
                    dataItem,
                    xslData.subList(dataItem.getRow(), xslData.size()).
                            stream().
                            map(rowData -> {
                                Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad source data column index %d", dataItem.getRow()));
                                return rowData[dataItem.getColumn()];
                            }).
                            collect(Collectors.toList())
            );
        }).collect(Collectors.toList());
    }

}
