package com.fgan.azure.fogbowmock;

import com.fgan.azure.api.IdentityApi;
import com.microsoft.azure.management.Azure;

public class AzureClient {

    // TODO(chico) - Finish
    public static Azure getAzure(AzureCloudUser azureCloudUser) {
        try {
            return IdentityApi.getAzure();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
