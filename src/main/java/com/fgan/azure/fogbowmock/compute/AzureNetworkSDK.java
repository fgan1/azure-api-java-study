package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkInterface;

public class AzureNetworkSDK {

    public static NetworkInterface getNetworkInterface(Azure azure, String azureNetworkInterfaceId)
            throws AzureException.ResourceNotFound {

        try {
            return azure.networkInterfaces().getById(azureNetworkInterfaceId);
        } catch (RuntimeException e) {
            throw new AzureException.ResourceNotFound(e);
        }
    }

}
