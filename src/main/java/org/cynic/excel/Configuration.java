package org.cynic.excel;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.util.Collections;

@SpringBootApplication
public class Configuration {
    @Bean
    JsonFactory jsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }


    @Autowired
    @Bean
    Credential googleDriveCredentials(@Value("${google.api.client_security_path}") String googleApiClientIdPath,
                                      @Value("${google.api.client_token_store}") String googleAuthTicketStorePath,
                                      JsonFactory jsonFactory) {
        try {
            try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(googleApiClientIdPath))) {
                GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(jsonFactory, reader);
                GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        jsonFactory,
                        googleClientSecrets,
                        Collections.singletonList(DriveScopes.DRIVE)).
                        setDataStoreFactory(new FileDataStoreFactory(new File(googleAuthTicketStorePath))).
                        setAccessType("offline").
                        build();

                return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, new LocalServerReceiver()) {
                    private final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCodeInstalledApp.class);

                    @Override
                    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                        LOGGER.warn("");
                        LOGGER.warn("****************************************************************");
                        LOGGER.warn("Please open this path in browser and authorize application: {}", authorizationUrl.build());
                        LOGGER.warn("****************************************************************");
                        LOGGER.warn("");
                    }
                }.authorize("application");
            } catch (GeneralSecurityException e) {
                throw new IllegalArgumentException("General HTTP security error while creating authorization flow", e);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read Google Drive Client ID secret", e);
        }
    }
}
