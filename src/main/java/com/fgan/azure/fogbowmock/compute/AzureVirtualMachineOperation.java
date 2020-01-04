package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;

public interface AzureVirtualMachineOperation<T> {

    void doCreateInstance(AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
                          AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.ResourceNotFound;

    String findVirtualMachineSizeName(int memoryRequired, int vCpuRequired, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.NoAvailableResourcesException;

    AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.ResourceNotFound;

    void doDeleteInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized;
}
