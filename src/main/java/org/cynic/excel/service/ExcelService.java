package org.cynic.excel.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Component
public class ExcelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelService.class);
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "text/csv",
            "application/vnd.ms-excel",
            "application/msexcel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
    private static final Detector CONTENT_TYPE_DETECTOR = new DefaultDetector();

    private final Credential credential;
    private final JsonFactory jsonFactory;


    @Autowired
    public ExcelService(Credential credential, JsonFactory jsonFactory) {
        this.credential = credential;
        this.jsonFactory = jsonFactory;
    }

    public final void validateFiles(Pair<String, byte[]> firstFile, Pair<String, byte[]> secondFile) {
        LOGGER.info("validateFiles({},{})", firstFile, secondFile);

        validateType(firstFile);
        validateType(secondFile);

/**
 * TODO: Validate
 * (1) structure
 *
 */

    }

    private void validateType(Pair<String, byte[]> fileData) {
        String mimeType = detectContentType(fileData.getValue());
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(
                    String.format("File '%s' type '%s' is not supported. Supported types are: '%s'", fileData.getKey(), mimeType, ALLOWED_MIME_TYPES)
            );
        }
    }

    public Pair<String, byte[]> mergeFiles(Pair<String, byte[]> firstFileData, Pair<String, byte[]> secondFileData) {
        LOGGER.info("mergeFiles({},{})", firstFileData, secondFileData);
/**
 * TODO: implement:
 *  (1) check file type by signature
 *  (2) define source and destination files
 *  (3) merge files by rule
 */

        return firstFileData;
    }

    public void saveFile(Pair<String, byte[]> mergedFileData) {
        LOGGER.info("saveFile({})", mergedFileData);
        Drive drive = connectToDrive(credential);

        try {
            AbstractInputStreamContent inputStreamContent = new ByteArrayContent(detectContentType(mergedFileData.getValue()), mergedFileData.getValue());
            drive.files().
                    create(new com.google.api.services.drive.model.File().
                                    setName(mergedFileData.getKey()),
                            inputStreamContent
                    ).
                    setFields("id").
                    execute();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to upload file to Google Drive", e);
        }
    }

    private String detectContentType(byte[] data) {
        try {
            return CONTENT_TYPE_DETECTOR.detect(new ByteArrayInputStream(data), new Metadata()).toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to detect MIME-TYPE of provided file", e);
        }
    }

    private Drive connectToDrive(Credential credential) {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential).
                    setApplicationName("application").
                    build();
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("General HTTP security error while creating connection to Google Drive", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to create trusted HTTP transport", e);
        }
    }
}
