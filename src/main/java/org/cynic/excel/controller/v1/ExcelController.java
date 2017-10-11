package org.cynic.excel.controller.v1;

import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.data.FileFormat;
import org.cynic.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Callable;

@RestController
public class ExcelController extends AbstractV1Controller {

    private final ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/merge-files")
    public Callable<ResponseEntity> mergeFiles(@RequestParam("sourceFile") MultipartFile sourceFile,
                                               @RequestParam("destinationFile") MultipartFile destinationFile) {

        return () -> {
            Pair<FileFormat, byte[]> sourceFileData = Pair.of(
                    excelService.getFileFormat(Pair.of(sourceFile.getOriginalFilename(), sourceFile.getBytes())),
                    sourceFile.getBytes()
            );

            Pair<FileFormat, byte[]> destinationFileData = Pair.of(
                    excelService.getFileFormat(Pair.of(destinationFile.getOriginalFilename(), destinationFile.getBytes())),
                    destinationFile.getBytes()
            );

            Pair<String, byte[]> mergedFileData = excelService.mergeFiles(sourceFileData, destinationFileData);
            excelService.saveFile(mergedFileData);

            return ResponseEntity.noContent().build();
        };
    }
}
