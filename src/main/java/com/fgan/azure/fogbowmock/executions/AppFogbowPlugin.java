package com.fgan.azure.fogbowmock.executions;

import cloud.fogbow.ras.core.models.UserData;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.image.AzureImageOperationSDK;
import com.fgan.azure.fogbowmock.image.AzureImageOperationUtil;
import com.fgan.azure.fogbowmock.util.AzureResourceToInstancePolicy;
import com.fgan.azure.util.PropertiesUtil;

import java.util.ArrayList;

/**
 * This section is dedicated to Azure Fogbow Plugin
 */
public class AppFogbowPlugin {

    public static void main( String[] args ) throws Exception {
        System.out.println("Hello Azure/Fogbow Plugins Api!");

        String filePropertiesPath = System.getenv(PropertiesUtil.COMPUTE_PLUGIN_PROPERTIES_ENV);

        String imageId = new StringBuilder()
                .append("Canonical")
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARETOR)
                .append("UbuntuServer")
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARETOR)
                .append("18.04-LTS")
                .toString();

        String id = "123456789101112131415";
        int diskSize = 33;
        ComputeOrder computeOrder = new ComputeOrder(
                id, null, "", "", "", "", 0, 0, diskSize, imageId, new ArrayList<UserData>(), "", new ArrayList<>());
        computeOrder.setInstanceId(AzureResourceToInstancePolicy.generateFogbowInstanceIdBy(computeOrder));
        SampleExecutionFogbowPlugin.start()
                .compute(filePropertiesPath)
                    .start()
//                        .create(computeOrder)
//                        .get(computeOrder)
                        .delete(computeOrder)
                    .end()
                .finish();

        runningForeverUntilYouStopIt();
    }

    public static void runningForeverUntilYouStopIt() throws InterruptedException {
        final int SLEEP_TIME = 10000;
        while (true) {
            Thread.sleep(SLEEP_TIME);
            System.out.println("I am alive !!");
        }
    }

}
