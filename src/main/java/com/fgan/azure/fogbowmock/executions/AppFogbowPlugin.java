package com.fgan.azure.fogbowmock.executions;

import com.fgan.azure.SampleExecution;
import com.fgan.azure.api.identity.IdentityApi;
import com.microsoft.azure.management.Azure;

/**
 * This section is dedicated to Azure Fogbow Plugin
 */
public class AppFogbowPlugin {

    public static void main( String[] args ) throws Exception {
        System.out.println("Hello Azure Api!");
        Azure azure = IdentityApi.getAzureFogbow();

        SampleExecutionFogbowPlugin.start()
                /**
                 * Compute(Fogbow Plugin)
                 */
                .finish();

        runningForever();
    }

    public static void runningForever() throws InterruptedException {
        final int SLEEP_TIME = 10000;
        while (true) {
            Thread.sleep(SLEEP_TIME);
            System.out.println("I am alive !!");
        }
    }

}
