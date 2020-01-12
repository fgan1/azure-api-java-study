package com.fgan.azure.fogbowmock.image;

import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;

import java.util.List;

public interface AzureImageOperation {

    List<AzureGetImageRef> getImagesRef(String regionName,
                                        List<String> allowedPublishersNames,
                                        AzureCloudUser azureCloudUser)
            throws UnexpectedException, NoAvailableResourcesException, UnauthenticatedUserException, UnauthenticatedUserException, NoAvailableResourcesException;
}
