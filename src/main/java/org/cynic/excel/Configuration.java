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
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
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
            try (Reader reader = new InputStreamReader(new FileInputStream(googleApiClientIdPath))) {
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
                    public Credential authorize(String userId) throws IOException {
                        return getFlow().loadCredential(userId);
                    }

                    @Override
                    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                        LOGGER.warn("Unable to read stored authorization token from {}", googleAuthTicketStorePath);

                        throw new IllegalArgumentException(String.format("This application wasn't authorized to access Google Drive. Please use this url to create credentials token: [%s]", authorizationUrl.build()));
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
