package org.cynic.excel.service.manager;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.cynic.excel.data.CellFormat;
import org.cynic.excel.data.CellItem;
import org.cynic.excel.data.config.DataItem;
import org.cynic.excel.data.config.RuleValues;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CsvFileManager implements FileManager {

    private final char csvSeparator;

    CsvFileManager(char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }

    @Override
    public List<CellItem> readConstraintValues(List<DataItem> items, byte[] source) {
        CSVReader csvReader = getCsvReader(source);

        try {
            List<String[]> csvData = csvReader.readAll();

            return items.parallelStream().
                    map(dataItem -> {
                        Validate.isTrue(csvData.size() > dataItem.getRow(), String.format("Bad constraint data row index '%d'. Provided source file has less rows.", dataItem.getRow()));

                        String[] rowData = csvData.get(dataItem.getRow());
                        Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad constraint data column index '%d'. Provided source file has less columns.", dataItem.getRow()));

                        return new CellItem(CellFormat.STRING, rowData[dataItem.getColumn()], dataItem);

                    }).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV source file format.", e);
        }
    }

    @Override
    public List<CellItem> readSourceData(List<RuleValues> values, byte[] source) {
        CSVReader csvReader = getCsvReader(source);

        try {
            List<String[]> csvData = csvReader.readAll();

            return values.stream().
                    flatMap(ruleValue -> {
                        DataItem startData = ruleValue.getStart();
                        Validate.isTrue(csvData.size() > startData.getRow(), String.format("Bad copy start data row index '%d'. Provided source file has less rows.", startData.getRow()));

                        AtomicInteger index = new AtomicInteger(startData.getRow());

                        return csvData.subList(startData.getRow(), csvData.size()).
                                stream().
                                map(rowData -> {
                                    Validate.isTrue(rowData.length > startData.getColumn(), String.format("Bad copy data start column index '%d'. Provided source file has less columns.", startData.getRow()));

                                    return new CellItem(CellFormat.STRING, rowData[startData.getColumn()], new DataItem(index.getAndIncrement(), startData.getColumn()));
                                }).
                                collect(Collectors.toList()).
                                stream();
                    }).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV source file format.", e);
        }
    }

    @Override
    public byte[] writeSourceData(List<CellItem> readSourceData, byte[] destination) {
        CSVReader csvReader = getCsvReader(destination);

        try {
            List<String[]> csvData = csvReader.readAll();

            readSourceData.forEach(cellItem -> {
                DataItem cellCoordinate = cellItem.getCoordinate();

                if (shouldExtendRows(csvData, cellCoordinate)) {
                    extendRows(csvData, cellCoordinate);
                }

                String[] rowData = csvData.get(cellCoordinate.getRow());

                if (shouldExtendColumns(cellCoordinate, rowData)) {
                    rowData = Arrays.copyOf(rowData, cellCoordinate.getColumn() + 1);
                }

                rowData[cellCoordinate.getColumn()] = cellItem.getValue().orElse(StringUtils.EMPTY).toString();
                csvData.set(cellCoordinate.getRow(), rowData);
            });

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(result));
            csvWriter.writeAll(csvData);
            csvWriter.flush();

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV destination file format.", e);
        }
    }

    private void extendRows(List<String[]> csvData, DataItem cellCoordinate) {
        IntStream.range(0, cellCoordinate.getRow() - csvData.size()+1).
                forEach(value -> csvData.add(new String[cellCoordinate.getColumn()]));
    }

    private boolean shouldExtendColumns(DataItem cellCoordinate, String[] rowData) {
        return rowData.length <= cellCoordinate.getColumn();
    }

    private boolean shouldExtendRows(List<String[]> csvData, DataItem cellCoordinate) {
        return csvData.size() <= cellCoordinate.getRow();
    }

    private CSVReader getCsvReader(byte[] source) {
        return new CSVReaderBuilder(new InputStreamReader(new ByteArrayInputStream(source))).withCSVParser(
                new CSVParserBuilder().
                        withSeparator(csvSeparator).
                        build()).
                build();
    }
}
