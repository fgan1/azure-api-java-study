package com.fgan.azure.fogbowmock.image;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ImageInstance;
import cloud.fogbow.ras.api.http.response.ImageSummary;
import cloud.fogbow.ras.core.plugins.interoperability.ImagePlugin;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.util.AzureConstants;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class AzureImagePlugin implements ImagePlugin<AzureCloudUser> {

    private final String resourceGroupName;
    private final AzureImageOperationSDK azureImageOperationSDK;

    public AzureImagePlugin(String confFilePath) {
        Properties properties = PropertiesUtil.readProperties(confFilePath);

        this.resourceGroupName = properties.getProperty(AzureConstants.DEFAULT_RESOURCE_GROUP_NAME_KEY);
        this.azureImageOperationSDK = new AzureImageOperationSDK();
    }

    @Override
    public List<ImageSummary> getAllImages(AzureCloudUser azureCloudUser) throws FogbowException {
        List<AzureGetImageRef> imagesRef = this.azureImageOperationSDK.getImagesRef(this.resourceGroupName, null, azureCloudUser);
        return imagesRef.stream()
                .map(imageRef -> AzureImageOperationUtil.buildImageSummaryBy(imageRef))
                .collect(Collectors.toList());
    }

    @Override
    public ImageInstance getImage(String imageId, AzureCloudUser azureCloudUser) throws FogbowException {
        List<AzureGetImageRef> imagesRef = this.azureImageOperationSDK.getImagesRef(this.resourceGroupName, null, azureCloudUser);
        AzureGetImageRef azureGetImageRef = imagesRef.stream()
                .filter(imageRef -> imageId.equals(AzureImageOperationUtil.convertToImageSummaryId(imageRef)))
                .findFirst()
                .orElseThrow(() -> new NoAvailableResourcesException(""));

        String id = AzureImageOperationUtil.convertToImageSummaryId(azureGetImageRef);
        String name = AzureImageOperationUtil.convertToImageSummaryName(azureGetImageRef);

        return new ImageInstance(id, name, 0, 0, 0, "");
    }
}
