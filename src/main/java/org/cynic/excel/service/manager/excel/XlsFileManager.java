package org.cynic.excel.service.manager.excel;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
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

public class XlsFileManager extends AbstractExcelFileManager {
    @Override
    public List<CellItem> readConstraintValues(List<DataItem> rules, byte[] source) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new ByteArrayInputStream(source));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            return rules.parallelStream().
                    map(dataItem -> {
                        Validate.isTrue(hssfSheet.getPhysicalNumberOfRows() > dataItem.getRow(), String.format(Locale.getDefault(), "Bad constraint data row index '%d'. Provided source file has less rows.", dataItem.getRow()));
                        HSSFRow hssfRow = hssfSheet.getRow(dataItem.getRow());

                        Validate.isTrue(hssfRow.getLastCellNum() > dataItem.getColumn(), String.format(Locale.getDefault(), "Bad constraint data column index '%d'. Provided source file has less columns.", dataItem.getColumn()));
                        HSSFCell hssfCell = hssfRow.getCell(dataItem.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                        CellFormat cellFormat = getCellFormat(hssfCell);

                        return new CellItem(cellFormat, getCellValue(cellFormat, hssfCell), dataItem);
                    }).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS source file format.", e);
        }
    }

    @Override
    public List<CellItem> readSourceData(List<RuleValues> values, byte[] source) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new ByteArrayInputStream(source));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            return values.stream().
                    flatMap(ruleValue -> {
                        DataItem startData = ruleValue.getStart();
                        Validate.isTrue(hssfSheet.getLastRowNum() > startData.getRow(), String.format(Locale.getDefault(), "Bad copy start data row index '%d'. Provided source file has less rows.", startData.getRow()));

                        List<Row> rows = IteratorUtils.toList(hssfSheet.rowIterator());
                        AtomicInteger index = new AtomicInteger(startData.getRow());

                        return rows.subList(startData.getRow(), rows.size()).
                                stream().
                                map(row -> {
                                    HSSFRow hssfRow = HSSFRow.class.cast(row);
                                    Validate.isTrue(hssfRow.getLastCellNum() > startData.getColumn(), String.format(Locale.getDefault(), "Bad copy data start column index '%d'. Provided source file has less columns.", startData.getColumn()));

                                    HSSFCell hssfCell = hssfRow.getCell(startData.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                    CellFormat cellFormat = getCellFormat(hssfCell);

                                    return new CellItem(cellFormat,
                                            getCellValue(cellFormat, hssfCell),
                                            new DataItem(index.getAndIncrement(), startData.getColumn()),
                                            StringUtils.substringBefore(hssfCell.getCellStyle().getDataFormatString(), ";")
                                    );
                                }).
                                collect(Collectors.toList()).
                                stream();
                    }).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS source file format.", e);
        }
    }

    @Override
    public byte[] writeSourceData(List<CellItem> readSourceData, byte[] destination) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new ByteArrayInputStream(destination));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            readSourceData.forEach(cellItem -> {
                DataItem cellCoordinate = cellItem.getCoordinate();

                HSSFRow hssfRow = Optional.ofNullable(hssfSheet.getRow(cellCoordinate.getRow())).
                        orElseGet(() -> hssfSheet.createRow(cellCoordinate.getRow()));
                HSSFCell hssfCell = hssfRow.getCell(cellCoordinate.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                cellItem.getValue().ifPresent(value -> {
                    setCellType(hssfCell, cellItem.getCellFormat());
                    setCellValue(hssfCell, cellItem.getCellFormat(), value);
                    setCellStyle(hssfCell, cellItem.getFormat().get());
                });
            });

            HSSFFormulaEvaluator.evaluateAllFormulaCells(hssfWorkbook);

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            hssfWorkbook.write(result);

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS destination file format.", e);
        }
    }
}
