package org.cynic.excel.service.manager;

import org.cynic.excel.data.FileFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileManagerFactory {
    private final char csvSeparator;

    public FileManagerFactory(@Value("${csv.separator}") char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }

    public FileManager getFileManager(FileFormat fileFormat) {
        switch (fileFormat) {
            case CSV:
                return new CsvFileManager(csvSeparator);
            case XLS:
                return new XlsFileManager();
            case XLSX:
                return new XlsxFileManager();
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown file format: %s", fileFormat.name())
                );
        }
    }
}
