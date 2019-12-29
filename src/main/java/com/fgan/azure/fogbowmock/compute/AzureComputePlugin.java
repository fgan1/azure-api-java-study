package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.ComputePlugin;

public class AzureComputePlugin implements ComputePlugin<AzureCloudUser> {

    @Override
    public String requestInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        return null;
    }

    @Override
    public ComputeInstance getInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        return null;
    }

    @Override
    public boolean isReady(String s) {
        return false;
    }

    @Override
    public boolean hasFailed(String s) {
        return false;
    }

    @Override
    public void deleteInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {

    }
}
