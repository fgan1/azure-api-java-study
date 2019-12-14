package com.fgan.azure;

import com.fgan.azure.api.IdentityApi;
import com.microsoft.azure.management.Azure;

/**
 * 1. You must export environment variables.
 * export AZURE_AUTH_LOCATION=src/main/resources/azureauth.properties
 * export AZURE_AUTH_LOCATION=src/main/resources/general.properties
 */
public class App {

    public static void main( String[] args ) throws Exception {
        System.out.println("Hello Azure!");
        Azure azure = IdentityApi.getAzure();

        String id = "";
        TestsExecution.run()
//                .runComputeSampleOne(azure)
//                .printComputeInformation(azure)
                .printVirtualMachineAlreadyCreatedInformation(azure, id)
                .deleteVirtualMachineAlreadyCreatedInformation(azure, id)
//                .printNetworkInformation(azure)
                .finish();
    }

}
