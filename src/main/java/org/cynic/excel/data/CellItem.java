package org.cynic.excel.data;

import org.cynic.excel.data.config.DataItem;

import java.util.Optional;

public class CellItem {
    private CellFormat cellFormat;
    private Object value;
    private DataItem coordinate;

    public CellItem(CellFormat cellFormat, Object value, DataItem coordinate) {
        this.cellFormat = cellFormat;
        this.value = value;
        this.coordinate = coordinate;
    }

    public CellFormat getCellFormat() {
        return cellFormat;
    }

    public Optional<Object> getValue() {
        return Optional.ofNullable(value);
    }

    public DataItem getCoordinate() {
        return coordinate;
    }
}
