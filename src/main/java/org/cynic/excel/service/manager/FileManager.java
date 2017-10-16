package org.cynic.excel.service.manager;

import org.cynic.excel.data.CellItem;
import org.cynic.excel.data.config.DataItem;
import org.cynic.excel.data.config.RuleValues;

import java.util.List;

public interface FileManager {

    /**
     * Read values for constraint
     *
     * @param rules  rules
     * @param source source file
     * @return values for constraint
     */
    List<CellItem> readConstraintValues(List<DataItem> rules, byte[] source);

    /**
     * Read data from source
     *
     * @param values reading values
     * @param source source file
     * @return readConstraintValues data
     */
    List<CellItem> readSourceData(List<RuleValues> values, byte[] source);

    /**
     * Paste readConstraintValues data into file
     *
     * @param readSourceData data
     * @param destination    destination file
     * @return merged file
     */
    byte[] writeSourceData(List<CellItem> readSourceData, byte[] destination);

//    protected List<Pair<DataItem, List<Pair<CellFormat, ?>>>> internalReadData(List<RuleValues> values, List<List<Pair<CellFormat, Object>>> xslData) {
//        return values.stream().map(ruleValues -> {
//            DataItem dataItem = ruleValues.getStart();
//
//            return Pair.<DataItem,
//                    List<Pair<CellFormat, ?>>>of(
//                    dataItem,
//                //    xslData.size() > dataItem.getRow() ?
//                      //      new ArrayList<>() :
//                            xslData.subList(dataItem.getRow(), xslData.size()).
//                                    stream().
//                                    map(rowData -> {
//                                        Validate.isTrue(rowData.size() > dataItem.getColumn(), String.format("Bad source data column index %d", dataItem.getRow()));
//                                        return rowData.get(dataItem.getColumn());
//                                    }).
//                                    collect(Collectors.toList())
//            );
//        }).collect(Collectors.toList());
//    }
}
