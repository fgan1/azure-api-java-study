package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;

public interface AzureVirtualMachineOperation<T> {

    void doCreateInstance(AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
                          AzureCloudUser azureCloudUser)
            throws FogbowException;

    String findVirtualMachineSizeName(int memoryRequired, int vCpuRequired, AzureCloudUser azureCloudUser)
            throws FogbowException;

    AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws FogbowException;

    void doDeleteInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws FogbowException;
}
