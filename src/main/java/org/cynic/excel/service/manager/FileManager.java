package org.cynic.excel.service.manager;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;
import org.cynic.excel.data.FieldFormat;

import java.util.ArrayList;
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
    public abstract List<Pair<FieldFormat, ?>> readConstraintValues(List<DataItem> items, byte[] source);

    /**
     * Read data from source
     *
     * @param values reading values
     * @param source source file
     * @return readConstraintValues data
     */
    public abstract List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readSourceData(List<RuleValues> values, byte[] source);

    /**
     * Paste readConstraintValues data into file
     *
     * @param readData    data
     * @param destination destination file
     * @return merged file
     */
    public abstract byte[] pasteReadData(List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readData, byte[] destination);

    protected List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> internalReadData(List<RuleValues> values, List<List<Pair<FieldFormat, Object>>> xslData) {
        return values.stream().map(ruleValues -> {
            DataItem dataItem = ruleValues.getStart();

            return Pair.<DataItem,
                    List<Pair<FieldFormat, ?>>>of(
                    dataItem,
                //    xslData.size() > dataItem.getRow() ?
                      //      new ArrayList<>() :
                            xslData.subList(dataItem.getRow(), xslData.size()).
                                    stream().
                                    map(rowData -> {
                                        Validate.isTrue(rowData.size() > dataItem.getColumn(), String.format("Bad source data column index %d", dataItem.getRow()));
                                        return rowData.get(dataItem.getColumn());
                                    }).
                                    collect(Collectors.toList())
            );
        }).collect(Collectors.toList());
    }
}
