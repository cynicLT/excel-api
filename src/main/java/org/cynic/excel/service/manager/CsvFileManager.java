package org.cynic.excel.service.manager;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

class CsvFileManager implements FileManager {

    @Override
    public List<String> readConstraintValues(List<DataItem> items, byte[] source) {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(source)));

        try {
            List<String[]> csvData = csvReader.readAll();

            return items.stream().map(dataItem -> {
                Validate.isTrue(csvData.size() > dataItem.getRow(), String.format("Bad constraint data column row %d", dataItem.getRow()));
                String[] rowData = csvData.get(dataItem.getRow());

                Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad constraint data column index %d", dataItem.getRow()));
                return rowData[dataItem.getColumn()];
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV file format.", e);
        }
    }

    @Override
    public List<Pair<DataItem, List<String>>> readSourceData(List<RuleValues> values, byte[] source) {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(source)));

        try {
            List<String[]> csvData = csvReader.readAll();

            return values.stream().map(ruleValues -> {
                DataItem dataItem = ruleValues.getStart();
                Validate.isTrue(csvData.size() > dataItem.getRow(), String.format("Bad source data column row %d", dataItem.getRow()));

                return Pair.of(
                        dataItem,
                        csvData.subList(dataItem.getRow(), csvData.size()).
                                stream().
                                map(rowData -> {
                                    Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad source data column index %d", dataItem.getRow()));
                                    return rowData[dataItem.getColumn()];
                                }).
                                collect(Collectors.toList())
                );
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid CSV file format.", e);
        }
    }

    @Override
    public byte[] pasteReadData(List<Pair<DataItem, List<String>>> readData, byte[] destination) {
        CSVReader csvReader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(destination)));

        try {
            List<String[]> csvData = csvReader.readAll();

            readData.forEach(dataItemListPair -> {
                DataItem dataItem = dataItemListPair.getKey();
                Validate.isTrue(csvData.size() > dataItem.getRow(), String.format("Bad source data column row %d", dataItem.getRow()));

                for (int rowIndex = dataItem.getRow(); rowIndex < csvData.size(); rowIndex++) {
                    String[] rowData = csvData.get(rowIndex);
                    Validate.isTrue(rowData.length > dataItem.getColumn(), String.format("Bad source data column index %d", dataItem.getRow()));
                    rowData[dataItem.getColumn()] = dataItemListPair.getValue().get(rowIndex - dataItem.getRow());
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
}
