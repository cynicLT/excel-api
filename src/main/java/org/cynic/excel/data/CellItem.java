package org.cynic.excel.data;

import org.apache.commons.lang3.StringUtils;
import org.cynic.excel.data.config.DataItem;

import java.util.Optional;

public class CellItem {
    private final CellFormat cellFormat;
    private final Object value;
    private final DataItem coordinate;
    private final String format;


    public CellItem(CellFormat cellFormat, Object value, DataItem coordinate) {
        this.cellFormat = cellFormat;
        this.value = value;
        this.coordinate = coordinate;
        this.format = null;
    }

    public CellItem(CellFormat cellFormat, Object value, DataItem coordinate, String format) {
        this.cellFormat = cellFormat;
        this.value = value;
        this.coordinate = coordinate;
        this.format = format;
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

    public Optional<String> getFormat() {
        return Optional.ofNullable(format);
    }
}
