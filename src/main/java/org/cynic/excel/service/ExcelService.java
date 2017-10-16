package org.cynic.excel.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.cynic.excel.data.CellItem;
import org.cynic.excel.data.FileFormat;
import org.cynic.excel.data.config.DataItem;
import org.cynic.excel.data.config.RuleConfiguration;
import org.cynic.excel.data.config.RulesConfiguration;
import org.cynic.excel.service.manager.FileManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExcelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelService.class);
    private static final String CSV_MIME_TYPE = "text/csv";
    private static final List<String> XLS_MIME_TYPE = Arrays.asList(
            "application/vnd.ms-excel",
            "application/msexcel");

    private static final List<String> ALLOWED_MIME_TYPES = new ArrayList<String>() {{
        add(CSV_MIME_TYPE);
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        addAll(XLS_MIME_TYPE);
    }};

    private static final Detector CONTENT_TYPE_DETECTOR = new DefaultDetector();
    private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    private final Credential credential;
    private final JsonFactory jsonFactory;
    private final FileManagerFactory fileManagerFactory;
    private final RulesConfiguration rulesConfiguration;

    @Autowired
    public ExcelService(RulesConfiguration rulesConfiguration,
                        Credential credential,
                        JsonFactory jsonFactory,
                        FileManagerFactory fileManagerFactory) {
        this.rulesConfiguration = rulesConfiguration;
        this.credential = credential;
        this.jsonFactory = jsonFactory;
        this.fileManagerFactory = fileManagerFactory;
    }

    public FileFormat getFileFormat(Pair<String, byte[]> fileData) {
        LOGGER.info("getFileFormat({})", fileData);
        String mimeType = detectContentType(fileData);

        if (ALLOWED_MIME_TYPES.contains(mimeType)) {
            return CSV_MIME_TYPE.equals(mimeType) ?
                    FileFormat.CSV :
                    XLS_MIME_TYPE.contains(mimeType) ?
                            FileFormat.XLS :
                            FileFormat.XLSX;
        } else {
            throw new IllegalArgumentException(
                    String.format("File '%s' type '%s' is not supported. Supported types are: '%s'", fileData.getKey(), mimeType, ALLOWED_MIME_TYPES)
            );
        }
    }

    public Pair<String, byte[]> mergeFiles(Pair<FileFormat, byte[]> sourceFileData, Pair<FileFormat, byte[]> destinationFileData) {
        LOGGER.info("mergeFiles({},{})", sourceFileData, destinationFileData);

        RuleConfiguration ruleConfiguration = getSourceConfiguration(sourceFileData);

        List<CellItem> sourceData = fileManagerFactory.getFileManager(sourceFileData.getKey()).
                readSourceData(ruleConfiguration.getValues(), sourceFileData.getValue());

        byte[] mergedFile = fileManagerFactory.getFileManager(destinationFileData.getKey()).
                writeSourceData(sourceData, destinationFileData.getRight());

        return Pair.of(
                String.format(
                        "%s-%s.%s",
                        ruleConfiguration.getName(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        destinationFileData.getKey().name().toLowerCase()
                ),
                mergedFile
        );
    }


    public void saveFile(Pair<String, byte[]> mergedFileData) {
        LOGGER.info("saveFile({})", mergedFileData);
        try {
            FileOutputStream d = new FileOutputStream("result.xlsx");
            d.write(mergedFileData.getRight());
            d.flush();
            d.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//
//
//        Drive drive = connectToDrive(credential);
//
//        try {
//            AbstractInputStreamContent inputStreamContent = new ByteArrayContent(detectContentType(mergedFileData), mergedFileData.getValue());
//            drive.files().
//                    create(new com.google.api.services.drive.model.File().
//                                    setName(mergedFileData.getKey()),
//                            inputStreamContent
//                    ).
//                    setFields("id").
//                    execute();
//        } catch (IOException e) {
//            throw new IllegalArgumentException("Unable to upload file to Google Drive", e);
//        }
    }


    private RuleConfiguration getSourceConfiguration(Pair<FileFormat, byte[]> sourceFileData) {
        return rulesConfiguration.
                getRules().
                stream().
                filter(rule -> {
                    List<CellItem> constraintData = readDataFromFile(sourceFileData, rule.getConstraint().getData());

                    return evaluateExpression(
                            rule.getConstraint().getExpression(),
                            constraintData.stream().
                                    map(cellItem -> cellItem.getValue().orElse(null)).
                                    collect(Collectors.toList()
                                    )
                    );
                }).
                findFirst().
                orElseThrow(() -> new IllegalArgumentException("Unable to find merging rule for provided source file."));
    }

    private boolean evaluateExpression(String expression, List<?> data) {
        ScriptEngine scriptEngine = Optional.ofNullable(SCRIPT_ENGINE_MANAGER.getEngineByName("nashorn")).
                orElseThrow(() -> new IllegalArgumentException("Unable to find scripting engine 'nashorn'. Check if JDK is 8"));

        try {
            scriptEngine.eval(String.format("function checkConstraint(data){ %s }", expression));

            return Boolean.class.cast(Invocable.class.cast(scriptEngine).invokeFunction("checkConstraint", data));
        } catch (ScriptException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Unable to execute constraint validation.", e);
        }
    }

    private List<CellItem> readDataFromFile(Pair<FileFormat, byte[]> sourceFileData, List<DataItem> constraintData) {
        return fileManagerFactory.getFileManager(sourceFileData.getKey()).
                readConstraintValues(constraintData, sourceFileData.getValue());
    }

    private String detectContentType(Pair<String, byte[]> fileData) {
        try {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, fileData.getKey());

            return CONTENT_TYPE_DETECTOR.detect(new ByteArrayInputStream(fileData.getValue()), metadata).toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to detect MIME-TYPE of provided file.", e);
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
