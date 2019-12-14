package com.fgan.azure.api;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.UserTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.rest.LogLevel;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.IOException;

public class IdentityApi {
    private final static String AZURE_AUTH_LOCATION = "AZURE_AUTH_LOCATION";

    // TODO(fgan): think about change to singleton pattern
    public static Azure getAzure() throws Exception {
        System.out.println("Getting azure object");

        // TODO(fgan): refactor to a method; move to Utils class
        String azureAuthLocation = getAzureAuthLocation();
        azureAuthLocation = "src/main/resources/azureauth.properties"; // TODO(fgan): remove this
        final File credFile = new File(azureAuthLocation);
        if (!credFile.exists()) {
            String errorMsg = String.format("There is no file on path: %s", azureAuthLocation);
            throw new Exception(errorMsg);
        }

        try {
            return Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(credFile)
                    .withDefaultSubscription();
        } catch (IOException e) {
            throw new Exception("Is not possible get Azure object", e);
        }
    }

    public static void authenticate(String clientId, String tenantId, String username, String password) {
        UserTokenCredentials credentials = new UserTokenCredentials(
                clientId,
                tenantId,
                username,
                password,
                AzureEnvironment.AZURE);
        Azure.Authenticated authenticate = Azure.authenticate(credentials);
    }

    // TODO(fgan): move to Utils class
    @Nullable
    private static String getAzureAuthLocation() {
        return System.getenv(AZURE_AUTH_LOCATION);
    }

}
