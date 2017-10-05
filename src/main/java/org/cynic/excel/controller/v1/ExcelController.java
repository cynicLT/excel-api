package org.cynic.excel.controller.v1;

import javafx.util.Pair;
import org.cynic.excel.controller.AbstractV1Controller;
import org.cynic.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.Callable;

@RestController
public class ExcelController extends AbstractV1Controller {

    private ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/merge-files")
    public Callable<byte[]> mergeFiles(@RequestParam("firstFile") MultipartFile firstFile, @RequestParam("secondFile") MultipartFile secondFile) throws IOException {
        Pair<String, byte[]> firstFileData = new Pair<>(firstFile.getOriginalFilename(), firstFile.getBytes());
        Pair<String, byte[]> secondFileData = new Pair<>(secondFile.getOriginalFilename(), secondFile.getBytes());

        excelService.validateFiles(firstFileData, secondFileData);

        return () -> excelService.mergeFiles(firstFileData, secondFileData);
    }
}
