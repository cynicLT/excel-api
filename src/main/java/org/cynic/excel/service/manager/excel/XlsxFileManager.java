package org.cynic.excel.service.manager.excel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;
import org.cynic.excel.data.FieldFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class XlsxFileManager extends AbstractExcelFileManager {
    @Override
    public List<Pair<FieldFormat, ?>> readConstraintValues(List<DataItem> items, byte[] source) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new ByteArrayInputStream(source));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            return items.stream().map(dataItem -> {
                Validate.isTrue(xssfSheet.getPhysicalNumberOfRows() > dataItem.getRow(), String.format("Bad constraint data column row %d", dataItem.getRow()));
                XSSFRow xssfRow = xssfSheet.getRow(dataItem.getRow());

                Validate.isTrue(xssfRow.getPhysicalNumberOfCells() > dataItem.getColumn(), String.format("Bad constraint data column index %d", dataItem.getRow()));
                XSSFCell xssfCell = xssfRow.getCell(dataItem.getColumn(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                FieldFormat fieldFormat = getFieldFormat(xssfCell);

                return Pair.of(fieldFormat, toFieldValue(fieldFormat, xssfCell));
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLSX file format.", e);
        }
    }


    @Override
    public List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readSourceData(List<RuleValues> values, byte[] source) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new ByteArrayInputStream(source));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            List<List<Pair<FieldFormat, Object>>> xslData = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(xssfSheet.rowIterator(), Spliterator.ORDERED), false).
                    map(row -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.cellIterator(), Spliterator.ORDERED), false).
                            map(XSSFCell.class::cast).
                            map(xssfCell -> {
                                FieldFormat fieldFormat = getFieldFormat(xssfCell);

                                return Pair.of(fieldFormat, toFieldValue(fieldFormat, xssfCell));
                            }).
                            collect(Collectors.toList())).
                    collect(Collectors.toList());
            return internalReadData(values, xslData);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLSX file format.", e);
        }
    }

    @Override
    public byte[] pasteReadData(List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readData, byte[] destination) {
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new ByteArrayInputStream(destination));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            readData.forEach(dataItemListPair -> {
                DataItem dataItem = dataItemListPair.getKey();
                Validate.isTrue(xssfSheet.getPhysicalNumberOfRows() > dataItem.getRow(), String.format("Bad constraint data column row %d", dataItem.getRow()));

                for (int rowIndex = dataItem.getRow(); rowIndex < xssfSheet.getPhysicalNumberOfRows(); rowIndex++) {
                    XSSFRow xssfRow = xssfSheet.getRow(rowIndex);

                    XSSFCell xssfCell = xssfRow.getCell(dataItem.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    if (CollectionUtils.isNotEmpty(dataItemListPair.getValue())) {
                        setCellValue(xssfCell, dataItemListPair.getValue().get(rowIndex - dataItem.getRow()));
                    }
                }
            });

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            xssfWorkbook.write(result);

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLSX file format.", e);
        }
    }
}
