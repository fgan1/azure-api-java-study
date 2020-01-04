package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureVirtualMachineRef;

public interface AzureVirtualMachineOperation<T> {

    void doCreateAsynchronously(AzureVirtualMachineRef azureVirtualMachineParameters,
                                AzureCloudUser azureCloudUser)
            throws FogbowException;

    String findFlavour(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException;

    ComputeInstance getComputeInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException;

    void doDeleteAsynchronously(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException;
}
