package com.fgan.azure.fogbowmock.executions;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.api.identity.IdentityApi;
import com.fgan.azure.fogbowmock.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.AzureComputePlugin;
import com.fgan.azure.util.PropertiesUtil;

public class SampleExecutionFogbowPlugin {

    private static SampleExecutionFogbowPlugin instanceSample = new SampleExecutionFogbowPlugin();

    public static SampleExecutionFogbowPlugin start() {
        System.out.println("**||||||||||||| Starting FOGBOW PLUGIN Sample Main Thread |||||||||||||**");
        return instanceSample;
    }

    public void finish() {
        System.out.println("**||||||||||||| Ending FOGBOW PLUGIN Sample Main Thread |||||||||||||**");
    }

    public static Compute compute(String computePropertiesPath) throws Exception {
        return new Compute(computePropertiesPath);
    }

    static class Compute extends TypeExecution<Compute> {

        private AzureComputePlugin azureComputePlugin;
        private AzureCloudUser azureCloudUser;

        Compute(String computePropertiesPath) throws Exception {
            this.azureComputePlugin = new AzureComputePlugin(computePropertiesPath);
            this.azureCloudUser = IdentityApi.getAzureCloudUser();;
        }

        public Compute create(ComputeOrder computeOrder) throws FogbowException {
            this.azureComputePlugin.requestInstance(computeOrder, this.azureCloudUser);
            return this;
        }

    }

    static class TypeExecution<T> {

        T start() {
            T specificType = (T) this;
            System.out.println(String.format("Start Excuting Type : %s", specificType.getClass().getSimpleName()));
            return specificType;
        }

        SampleExecutionFogbowPlugin end() {
            System.out.println("End Excuting Type");
            return SampleExecutionFogbowPlugin.instanceSample;
        }

    }

}
