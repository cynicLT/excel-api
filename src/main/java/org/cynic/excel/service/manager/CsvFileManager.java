package org.cynic.excel.service.manager;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;
import org.cynic.excel.data.FieldFormat;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CsvFileManager extends FileManager {

    private final char csvSeparator;

    CsvFileManager(char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }

    @Override
    public List<Pair<FieldFormat, ?>> readConstraintValues(List<DataItem> items, byte[] source) {
        CSVReader csvReader = getCsvReader(source);

        try {
            List<String[]> csvData = csvReader.readAll();

            return items.stream().map(dataItem -> {
                Validate.isTrue(csvData.size() > dataItem.getRow(), String.format("Bad constraint data column row %d", dataItem.getRow()));
                String[] rowData = csvData.get(dataItem.getRow());

                Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad constraint data column index %d", dataItem.getRow()));
                return Pair.of(FieldFormat.STRING, rowData[dataItem.getColumn()]);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV file format.", e);
        }
    }

    @Override
    public List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readSourceData(List<RuleValues> values, byte[] source) {
        CSVReader csvReader = getCsvReader(source);

        try {
            return internalReadData(values, toFiledTypeList(csvReader.readAll()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV file format.", e);
        }
    }

    private List<List<Pair<FieldFormat, Object>>> toFiledTypeList(List<String[]> values) {
        return values.stream().
                map(row -> Stream.of(row).
                        map(cell -> Pair.of(FieldFormat.STRING, Object.class.cast(cell))).
                        collect(Collectors.toList())).
                collect(Collectors.toList());
    }

    @Override
    public byte[] pasteReadData(List<Pair<DataItem, List<Pair<FieldFormat, ?>>>> readData, byte[] destination) {
        CSVReader csvReader = getCsvReader(destination);

        try {
            List<String[]> csvData = csvReader.readAll();

            readData.forEach(dataItemListPair -> {
                DataItem dataItem = dataItemListPair.getKey();
                Validate.isTrue(csvData.size() > dataItem.getRow(), String.format("Bad source data column row %d", dataItem.getRow()));

                for (int rowIndex = dataItem.getRow(); rowIndex < csvData.size(); rowIndex++) {
                    String[] rowData = csvData.get(rowIndex);
                    Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad source data column index %d", dataItem.getRow()));
                    rowData[dataItem.getColumn()] = String.valueOf(dataItemListPair.getValue().get(rowIndex - dataItem.getRow()).getValue());
                }
            });

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(result));
            csvWriter.writeAll(csvData);
            csvWriter.flush();

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV file format.", e);
        }
    }

    private CSVReader getCsvReader(byte[] source) {
        return new CSVReaderBuilder(new InputStreamReader(new ByteArrayInputStream(source))).withCSVParser(
                new CSVParserBuilder().
                        withSeparator(csvSeparator).
                        build()).
                build();
    }
}
