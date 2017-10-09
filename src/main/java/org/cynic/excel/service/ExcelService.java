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
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleConfiguration;
import org.cynic.excel.config.RulesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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
    private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    private final Credential credential;
    private final JsonFactory jsonFactory;
    private final RulesConfiguration rulesConfiguration;


    @Autowired
    public ExcelService(RulesConfiguration rulesConfiguration, Credential credential, JsonFactory jsonFactory) {
        this.rulesConfiguration = rulesConfiguration;
        this.credential = credential;
        this.jsonFactory = jsonFactory;
    }

    public final void validateFiles(Pair<String, byte[]> firstFile, Pair<String, byte[]> secondFile) {
        LOGGER.info("validateFiles({},{})", firstFile, secondFile);

        validateType(firstFile);
        validateType(secondFile);
    }

    public Pair<String, byte[]> mergeFiles(Pair<String, byte[]> firstFileData, Pair<String, byte[]> secondFileData) {
        LOGGER.info("mergeFiles({},{})", firstFileData, secondFileData);
/**
 * TODO:
 * check which is source which is destination
 */
        RuleConfiguration ruleConfiguration = rulesConfiguration.
                getRules().
                stream().
                filter(rule -> {
                    List<String> data = readDataFromFile(firstFileData, rule.getConstraint().getData());
                    return evaluateExpression(rule.getConstraint().getExpression(), data);
                }).
                findFirst().
                orElseThrow(() -> new IllegalArgumentException("Unable to find mapping rules to provides files"));

        return copyData(ruleConfiguration, firstFileData, secondFileData);
    }

    private Pair<String, byte[]> copyData(RuleConfiguration ruleConfiguration, Pair<String, byte[]> firstFileData, Pair<String, byte[]> secondFileData) {
        /**
         * TODO: copy data
         */
        return firstFileData;
    }

    private boolean evaluateExpression(String expression, List<String> data) {
        ScriptEngine scriptEngine = SCRIPT_ENGINE_MANAGER.getEngineByName("nashorn");

        try {
            scriptEngine.eval(String.format("function checkConstraint(data){ %s}", expression));

            return Boolean.class.cast(Invocable.class.cast(scriptEngine).invokeFunction("checkConstraint", data));
        } catch (ScriptException | NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Unable to execute constraint validation. Detailed message: %s", e.getMessage()));
        }
    }

    private List<String> readDataFromFile(Pair<String, byte[]> firstFileData, List<DataItem> constraint) {
/**
 * TODO: read data from file
 */
        return null;
    }

    public void saveFile(Pair<String, byte[]> mergedFileData) {
        LOGGER.info("saveFile({})", mergedFileData);
        Drive drive = connectToDrive(credential);

        try {
            AbstractInputStreamContent inputStreamContent = new ByteArrayContent(detectContentType(mergedFileData), mergedFileData.getValue());
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

    private void validateType(Pair<String, byte[]> fileData) {
        String mimeType = detectContentType(fileData);
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(
                    String.format("File '%s' type '%s' is not supported. Supported types are: '%s'", fileData.getKey(), mimeType, ALLOWED_MIME_TYPES)
            );
        }
    }

    private String detectContentType(Pair<String, byte[]> fileData) {
        try {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, fileData.getKey());

            return CONTENT_TYPE_DETECTOR.detect(new ByteArrayInputStream(fileData.getValue()), metadata).toString();
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
