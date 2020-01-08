package com.fgan.azure.fogbowmock.image;

import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;

import java.util.List;

public interface AzureImageOperation {

    List<AzureGetImageRef> getImagesRef(String regionName,
                                        List<String> allowedPublishersNames,
                                        AzureCloudUser azureCloudUser)
            throws AzureException.Unexpected, AzureException.NoAvailableResources, AzureException.Unauthenticated;
}
