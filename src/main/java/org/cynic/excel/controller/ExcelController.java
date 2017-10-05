package org.cynic.excel.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ExcelController {

    @PostMapping("/merge-files")
    public byte[] mergeFiles(@RequestParam("firstFile") MultipartFile firstFile, @RequestParam("secondFile") MultipartFile secondFile) throws IOException {

        return firstFile.getBytes();
    }
}
