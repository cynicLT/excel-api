package org.cynic.excel.service.manager.excel;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.cynic.excel.data.CellFormat;
import org.cynic.excel.data.CellItem;
import org.cynic.excel.data.config.DataItem;
import org.cynic.excel.data.config.RuleValues;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class XlsxFileManager extends AbstractExcelFileManager {
    @Override
    public List<CellItem> readConstraintValues(List<DataItem> rules, byte[] source) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new ByteArrayInputStream(source));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            return rules.parallelStream().
                    map(dataItem -> {
                        Validate.isTrue(xssfSheet.getPhysicalNumberOfRows() > dataItem.getRow(), String.format(Locale.getDefault(), "Bad constraint data row index '%d'. Provided source file has less rows.", dataItem.getRow()));
                        XSSFRow xssfRow = xssfSheet.getRow(dataItem.getRow());

                        Validate.isTrue(xssfRow.getPhysicalNumberOfCells() > dataItem.getColumn(), String.format(Locale.getDefault(), "Bad constraint data column index '%d'. Provided source file has less columns.", dataItem.getColumn()));
                        XSSFCell xssfCell = xssfRow.getCell(dataItem.getColumn(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                        CellFormat cellFormat = getCellFormat(xssfCell);

                        return new CellItem(cellFormat, getCellValue(cellFormat, xssfCell), dataItem);
                    }).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLSX source file format.", e);
        }
    }

    @Override
    public List<CellItem> readSourceData(List<RuleValues> values, byte[] source) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new ByteArrayInputStream(source));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            return values.stream().
                    flatMap(ruleValue -> {
                        DataItem startData = ruleValue.getStart();
                        Validate.isTrue(
                                xssfSheet.getPhysicalNumberOfRows() > startData.getRow(),
                                String.format(Locale.getDefault(), "Bad copy start data row index '%d'. Provided source file has less rows.", startData.getRow())
                        );

                        List<Row> rows = IteratorUtils.toList(xssfSheet.rowIterator());
                        AtomicInteger index = new AtomicInteger(startData.getRow());

                        return rows.subList(startData.getRow(), rows.size()).
                                stream().
                                map(row -> {
                                    XSSFRow xssfRow = XSSFRow.class.cast(row);
                                    Validate.isTrue(
                                            xssfRow.getPhysicalNumberOfCells() > startData.getColumn(),
                                            String.format(Locale.getDefault(), "Bad copy data start column index '%d'. Provided source file has less columns.",
                                                    startData.getColumn())
                                    );

                                    XSSFCell xssfCell = xssfRow.getCell(startData.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                    CellFormat cellFormat = getCellFormat(xssfCell);

                                    return new CellItem(cellFormat, getCellValue(cellFormat, xssfCell), new DataItem(index.getAndIncrement(), startData.getColumn()));
                                }).
                                collect(Collectors.toList()).
                                stream();
                    }).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLSX source file format.", e);
        }
    }

    @Override
    public byte[] writeSourceData(List<CellItem> readSourceData, byte[] destination) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new ByteArrayInputStream(destination));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            readSourceData.forEach(cellItem -> {
                DataItem cellCoordinate = cellItem.getCoordinate();

                XSSFRow xssfRow = Optional.ofNullable(xssfSheet.getRow(cellCoordinate.getRow())).
                        orElseGet(() -> xssfSheet.createRow(cellCoordinate.getRow()));
                XSSFCell xssfCell = xssfRow.getCell(cellCoordinate.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                cellItem.getValue().ifPresent(value -> setCellValue(xssfCell, cellItem.getCellFormat(), value));
            });

            XSSFFormulaEvaluator.evaluateAllFormulaCells(xssfWorkbook);

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            xssfWorkbook.write(result);

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS destination file format.", e);
        }
    }
}
