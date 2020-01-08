package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;

public interface AzureVirtualMachineOperation<T> {

    void doCreateInstance(AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
                          AzureCloudUser azureCloudUser)
            throws AzureException.Unauthenticated, AzureException.Unexpected, AzureException.ResourceNotFound;

    String findVirtualMachineSize(int memoryRequired, int vCpuRequired,
                                  String regionName, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthenticated, AzureException.NoAvailableResources, AzureException.Unexpected;

    AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, String regionName,
                                            AzureCloudUser azureCloudUser)
            throws AzureException.Unauthenticated, AzureException.Unexpected, AzureException.NoAvailableResources, AzureException.ResourceNotFound;

    void doDeleteInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthenticated, AzureException.ResourceNotFound, AzureException.Unexpected;
}
