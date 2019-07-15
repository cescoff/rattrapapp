package com.rattrap.google.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ImmutableList;
import com.rattrap.utils.Log4JConfigurationHelper;
import com.rattrap.utils.LogConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SheetsClient {
    private static final String APPLICATION_NAME = "Rattrap JAVA sheets exposer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_NAME = "credentials.json";

    private File getCredentialsFile() {
        final String systemPropsFilePath = System.getProperty("gdata.credentials.file.path");
        if (StringUtils.isNotEmpty(systemPropsFilePath)) {
            final File candidate = new File(systemPropsFilePath);
            if (!candidate.exists()) {
                throw new IllegalArgumentException("Property '-Dgdata.credentials.file.path'='" + systemPropsFilePath + "' is wrong : file does not exist");
            }
            return candidate;
        }
        final File configDir = new File("config");
        if (configDir.exists()) {
            final File candidate = new File(configDir, CREDENTIALS_FILE_NAME);
            if (!candidate.exists()) {
                LoggerFactory.getLogger(SheetsClient.class).info("Configuration file '{}' does not exist", configDir.getAbsolutePath());
            } else {
                return candidate;
            }
        } else {
            LoggerFactory.getLogger(SheetsClient.class).info("Configuration directory '{}' does not exist", configDir.getAbsolutePath());
        }
        throw new IllegalArgumentException("Missing credential file");
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.

        InputStream in = new FileInputStream(getCredentialsFile());

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Iterable<ProjectLaunch> getLaunches() throws IOException, GeneralSecurityException {
        final ImmutableList.Builder<ProjectLaunch> result = ImmutableList.builder();
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1I3mmF40pFxSjskfl4tiunjGRbisGs0GJ8l2nW0Y2lC8";
        final String range = "SAP!A2:E";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values) {
                final Object idObj = row.get(0);
                final Object nameObj = row.get(1);
                final Object familyObj = row.get(2);

                final String id;
                final String name;
                final String family;
                if (idObj != null) {
                    id = idObj.toString();
                } else {
                    id = null;
                }

                if (nameObj != null) {
                    name = nameObj.toString();
                } else {
                    name = null;
                }

                if (familyObj != null) {
                    family = familyObj.toString();
                } else {
                    family = null;
                }

                result.add(new ProjectLaunch(id, name, family));
                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row.get(0), row.get(4));
            }
        }
        return result.build();
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        new Log4JConfigurationHelper(new LogConfig("INFO", "run", "test.log",
                true)).configure();
        for (final ProjectLaunch projectLaunch : new SheetsClient().getLaunches()) {
            System.out.println("ID='" + projectLaunch.getId() + "'");
        }

    }
}