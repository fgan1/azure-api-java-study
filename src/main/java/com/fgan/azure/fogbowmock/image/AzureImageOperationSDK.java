package com.fgan.azure.fogbowmock.image;

import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineOffer;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.compute.VirtualMachineSku;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

import java.util.ArrayList;
import java.util.List;

public class AzureImageOperationSDK implements AzureImageOperation {

    private static List<AzureGetImageRef> recoverImages(Azure azure, Region region, List<String> allowedPublishersNames)
            throws AzureException.Unexpected, AzureException.NoAvailableResources {

        List<AzureGetImageRef> azureVirtualMachineImages = new ArrayList<>();
        PagedList<VirtualMachinePublisher> imagePublishersByRegion = AzureVirtualMachineImageSDK.getImagePublishersByRegion(azure, region);
        imagePublishersByRegion.stream()
                .filter(publisher -> allowedPublishersNames.contains(publisher.name()))
                .forEach(publisher -> {
                    PagedList<VirtualMachineOffer> virtualMachineOffers = publisher.offers().list();
                    virtualMachineOffers.stream()
                            .forEach(offer -> {
                                PagedList<VirtualMachineSku> virtualMachineSkus = offer.skus().list();
                                virtualMachineSkus.stream()
                                        .forEach(sku -> {
                                            AzureGetImageRef azureVirtualMachineImage =
                                                    new AzureGetImageRef(publisher.name(), offer.name(), sku.name());
                                            azureVirtualMachineImages.add(azureVirtualMachineImage);
                                            System.out.println(azureVirtualMachineImage.toString());
                                        });
                            });
                });

        if (azureVirtualMachineImages.isEmpty()) throw new AzureException.NoAvailableResources("");

        return azureVirtualMachineImages;
    }

    @Override
    public List<AzureGetImageRef> getImagesRef(String regionName,
                                               List<String> allowedPublishersNames,
                                               AzureCloudUser azureCloudUser)
            throws AzureException.Unexpected, AzureException.NoAvailableResources, AzureException.Unauthenticated {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        Region region = Region.findByLabelOrName(regionName);
        return recoverImages(azure, region, allowedPublishersNames);
    }
}
