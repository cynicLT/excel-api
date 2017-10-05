package org.cynic.excel.controller.v1;

import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.controller.AbstractV1Controller;
import org.cynic.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public Callable<ResponseEntity> mergeFiles(@RequestParam("firstFile") MultipartFile firstFile, @RequestParam("secondFile") MultipartFile secondFile) throws IOException {
        Pair<String, byte[]> firstFileData = Pair.of(firstFile.getOriginalFilename(), firstFile.getBytes());
        Pair<String, byte[]> secondFileData = Pair.of(secondFile.getOriginalFilename(), secondFile.getBytes());


        return () -> {
            excelService.validateFiles(firstFileData, secondFileData);
            Pair<String, byte[]> mergedFileData = excelService.mergeFiles(firstFileData, secondFileData);
            excelService.saveFile(mergedFileData);

            return ResponseEntity.noContent().build();
        };
    }
}
