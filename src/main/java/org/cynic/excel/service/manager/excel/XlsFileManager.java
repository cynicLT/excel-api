package org.cynic.excel.service.manager.excel;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

public class XlsFileManager extends AbstractExcelFileManager {
    @Override
    public List<Pair<FieldFormat, ?>> readConstraintValues(List<DataItem> items, byte[] source) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new ByteArrayInputStream(source));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            return items.stream().map(dataItem -> {
                Validate.isTrue(hssfSheet.getPhysicalNumberOfRows() > dataItem.getRow(), String.format("Bad constraint data column row %d", dataItem.getRow()));
                HSSFRow hssfRow = hssfSheet.getRow(dataItem.getRow());

                Validate.isTrue(hssfRow.getPhysicalNumberOfCells() > dataItem.getColumn(), String.format("Bad constraint data column index %d", dataItem.getRow()));
                HSSFCell hssfCell = hssfRow.getCell(dataItem.getColumn(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                FieldFormat fieldFormat = getFieldFormat(hssfCell);

                return Pair.of(fieldFormat, toFieldValue(fieldFormat, hssfCell));
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS file format.", e);
        }
    }

    @Override
    public List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readSourceData(List<RuleValues> values, byte[] source) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new ByteArrayInputStream(source));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            List<List<Pair<FieldFormat, Object>>> xslData = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(hssfSheet.rowIterator(), Spliterator.ORDERED), false).
                    map(row -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.cellIterator(), Spliterator.ORDERED), false).
                            map(HSSFCell.class::cast).
                            map(hssfCell -> {
                                FieldFormat fieldFormat = getFieldFormat(hssfCell);

                                return Pair.of(fieldFormat, toFieldValue(fieldFormat, hssfCell));
                            }).
                            collect(Collectors.toList())).
                    collect(Collectors.toList());
            return internalReadData(values, xslData);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS file format.", e);
        }
    }


    @Override
    public byte[] pasteReadData(List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readData, byte[] destination) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new ByteArrayInputStream(destination));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            readData.forEach(dataItemListPair -> {
                DataItem dataItem = dataItemListPair.getKey();
                Validate.isTrue(hssfSheet.getPhysicalNumberOfRows() > dataItem.getRow(), String.format("Bad constraint data column row %d", dataItem.getRow()));

                for (int rowIndex = dataItem.getRow(); rowIndex < hssfSheet.getPhysicalNumberOfRows(); rowIndex++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowIndex);

                    HSSFCell hssfCell = hssfRow.getCell(dataItem.getColumn(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    setCellValue(hssfCell, dataItemListPair.getValue().get(rowIndex - dataItem.getRow()));
                }
            });

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            hssfWorkbook.write(result);

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid XLS file format.", e);
        }
    }


}
