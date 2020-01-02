package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.AzureCloudUser;

public interface AzureVirtualMachineOperation<T> {

    void doCreateAsynchronously(AzureVirtualMachineParameters azureVirtualMachineParameters,
                                AzureCloudUser azureCloudUser)
            throws FogbowException;

    String findFlavour(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException;

    ComputeInstance getComputeInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException;

    void doDeleteAsynchronously(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException;
}
