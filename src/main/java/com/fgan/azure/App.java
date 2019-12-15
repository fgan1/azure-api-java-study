package com.fgan.azure;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.IdentityApi;
import com.microsoft.azure.management.Azure;

/**
 * Project create in order to learn/test how to use Azure Java Api/JDK;
 *
 * Steps:
 * 1. You must export environment variables.
 * export AZURE_AUTH_LOCATION=src/main/resources/azureauth.properties
 * export AZURE_AUTH_LOCATION=src/main/resources/general.properties
 */
public class App {

    public static void main( String[] args ) throws Exception {
        System.out.println("Hello Azure Api!");
        Azure azure = IdentityApi.getAzure();

        String id = "";
        String name = "";
        Execution.start()
                /**
                 * Compute(Fogbow naming) / Virtual Machine(Azure naming)
                 */
//                .runComputeSampleOne(azure)
//                .createComputeStyleFogbow(azure)
//                .printComputeInformation(azure)
//                .printVirtualMachineAlreadyCreatedInformation(azure, id)
//                .deleteVirtualMachineAlreadyCreated(azure, id)
//                .deleteVirtualMachineAlreadyCreatedByIdAsync(azure, id)
                .deleteVirtualMachineAlreadyCreatedByNameAsync(azure, name)
                /**
                 * Network
                 */
//                .printNetworkInformation(azure)
                /**
                 * Volume(Fogbow naming) / Disk(Azure namming)
                 */
//                .printVolumeInformation(azure)
//                .printDiskCreatedInformation(azure, id)
//                .deleteDiskCreatedById(azure, id)
//                .deleteDiskCreatedByName(azure, name)
//                .deleteDiskByVirtualMachine(azure, id)
                .finish();

        runningForever();
    }

    public static void runningForever() throws InterruptedException {
        final int SLEEP_TIME = 30000;
        while (true) {
            Thread.sleep(SLEEP_TIME);
            System.out.println("I am alive !!");
        }
    }

}
