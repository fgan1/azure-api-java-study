package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;

public interface AzureVirtualMachineOperation<T> {

    void doCreateInstance(AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
                          AzureCloudUser azureCloudUser)
            throws UnauthenticatedUserException, UnexpectedException, InstanceNotFoundException,
            InstanceNotFoundException;

    String findVirtualMachineSize(int memoryRequired, int vCpuRequired,
                                  String regionName, AzureCloudUser azureCloudUser)
            throws UnauthenticatedUserException, NoAvailableResourcesException, UnexpectedException;

    AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws UnauthenticatedUserException, UnexpectedException, NoAvailableResourcesException,
            InstanceNotFoundException, InstanceNotFoundException;

    void doDeleteInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws UnauthenticatedUserException, InstanceNotFoundException, UnexpectedException,
            InstanceNotFoundException;
}
