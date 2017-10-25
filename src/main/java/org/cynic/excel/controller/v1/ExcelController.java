package org.cynic.excel.controller.v1;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.data.FileFormat;
import org.cynic.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Locale;
import java.util.zip.Deflater;

@RestController
public class ExcelController extends AbstractV1Controller {

    private final ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/merge-files")
    public ResponseEntity<?> mergeFilesGoogle(@RequestParam("sourceFile") MultipartFile sourceFile,
                                              @RequestParam("destinationFile") MultipartFile destinationFile) throws IOException {

        Pair<String, byte[]> mergedFileData = mergeFiles(sourceFile, destinationFile);
        excelService.saveFile(mergedFileData);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/merge-files-instant")
    public ResponseEntity<byte[]> mergeFilesInstant(@RequestParam("sourceFile") MultipartFile sourceFile,
                                                    @RequestParam("destinationFile") MultipartFile destinationFile) throws IOException {

        Pair<String, byte[]> mergedFileData = mergeFiles(sourceFile, destinationFile);

        return ResponseEntity.ok().
                contentType(new MediaType("application", "zip")).
                header("Content-Disposition",
                        String.format(
                                Locale.getDefault(),
                                "attachment; filename=\"%s.zip\"",
                                StringUtils.substringBeforeLast(mergedFileData.getKey(), ".")
                        )
                ).
                body(zipResponse(mergedFileData));

    }

    private Pair<String, byte[]> mergeFiles(@RequestParam("sourceFile") MultipartFile sourceFile, @RequestParam("destinationFile") MultipartFile destinationFile) throws IOException {
        Pair<FileFormat, byte[]> sourceFileData = Pair.of(
                excelService.getFileFormat(Pair.of(sourceFile.getOriginalFilename(), sourceFile.getBytes())),
                sourceFile.getBytes()
        );

        Pair<FileFormat, byte[]> destinationFileData = Pair.of(
                excelService.getFileFormat(Pair.of(destinationFile.getOriginalFilename(), destinationFile.getBytes())),
                destinationFile.getBytes()
        );

        return excelService.mergeFiles(sourceFileData, destinationFileData);
    }

    private byte[] zipResponse(Pair<String, byte[]> mergedFileData) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(result);
            zipArchiveOutputStream.setLevel(Deflater.BEST_COMPRESSION);

            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(mergedFileData.getKey());
            zipArchiveEntry.setSize(Array.getLength(mergedFileData.getValue()));
            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
            zipArchiveOutputStream.write(mergedFileData.getValue());
            zipArchiveOutputStream.closeArchiveEntry();
            zipArchiveOutputStream.close();

            return result.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to zip merged file", e);
        }
    }
}
