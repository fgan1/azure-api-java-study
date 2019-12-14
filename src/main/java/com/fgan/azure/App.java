package com.fgan.azure;

import com.fgan.azure.api.IdentityApi;
import com.microsoft.azure.management.Azure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1. You must export environment variables.
 * export AZURE_AUTH_LOCATION=src/main/resources/azureauth.properties
 * export AZURE_AUTH_LOCATION=src/main/resources/general.properties
 */
public class App {
    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws Exception {
        LOGGER.info("Hello Azure!");
        Azure azure = IdentityApi.getAzure();

        TestsExecution.run()
                .printNetworkInformation(azure)
                .finish();
    }

}
