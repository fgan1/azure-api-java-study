package com.fgan.azure.fogbowmock.network;

import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.network.model.AzureCreateNetworkRef;

public interface AzureNetworkOperational {

    void doCreateInstance(AzureCreateNetworkRef azureCreateNetworkRef, AzureCloudUser azureCloudUser);

    void doGetInstance(String instanceId, AzureCloudUser azureCloudUser);

    void doDeleteInstance(String instanceId, AzureCloudUser azureCloudUser);

}
