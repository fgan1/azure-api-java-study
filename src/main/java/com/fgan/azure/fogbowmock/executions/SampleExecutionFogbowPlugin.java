package com.fgan.azure.fogbowmock.executions;

import cloud.fogbow.common.models.CloudUser;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.ComputePlugin;
import com.fgan.azure.fogbowmock.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.AzureComputePlugin;

public class SampleExecutionFogbowPlugin {

    private static SampleExecutionFogbowPlugin instance = new SampleExecutionFogbowPlugin();

    public static SampleExecutionFogbowPlugin start() {
        System.out.println("**||||||||||||| Starting FOGBOW PLUGIN Sample Main Thread |||||||||||||**");
        return instance;
    }

    public void finish() {
        System.out.println("**||||||||||||| Ending FOGBOW PLUGIN Sample Main Thread |||||||||||||**");
    }

    public static Compute compute() {
        return new Compute();
    }

    static class Compute {

        AzureComputePlugin azureComputePlugin;

        Compute() {
            this.azureComputePlugin = new AzureComputePlugin();
        }

        public void create(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) {

        }

    }

}
