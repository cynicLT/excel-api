package org.cynic.excel.service.manager;

import org.cynic.excel.data.FileFormat;
import org.springframework.stereotype.Component;

@Component
public class FileManagerFactory {
    public FileManager getFileManager(FileFormat fileFormat) {
        switch (fileFormat) {
            case CSV:
                return new CsvFileManager();
            case XLS:
                return new XlsFileManager();
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown file format: %s", fileFormat.name())
                );
        }
    }
}
