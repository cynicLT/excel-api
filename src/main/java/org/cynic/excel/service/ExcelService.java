package org.cynic.excel.service;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExcelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelService.class);

    public final void validateFiles(Pair<String, byte[]> firstFile, Pair<String, byte[]> secondFile) {
        LOGGER.info("validateFiles({},{})", firstFile, secondFile);

//        TODO: implement
    }

    public byte[] mergeFiles(Pair<String, byte[]> firstFileData, Pair<String, byte[]> secondFileData) {
        LOGGER.info("mergeFiles({},{})", firstFileData, secondFileData);
//        TODO: implement

        return firstFileData.getValue();

    }
}
