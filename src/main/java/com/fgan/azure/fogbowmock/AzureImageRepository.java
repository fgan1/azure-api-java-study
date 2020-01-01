package com.fgan.azure.fogbowmock;

import cloud.fogbow.ras.api.http.response.ImageSummary;
import com.fgan.azure.api.image.ImageApi;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineOffer;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.compute.VirtualMachineSku;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AzureImageRepository {

    @VisibleForTesting static final String IMAGE_SUMMARY_ID_SEPARETOR = ":|:";
    @VisibleForTesting static final String IMAGE_SUMMARY_NAME_SEPARETOR = " - ";

    public static ImageSummary buildImageSummaryBy(AzureVirtualMachineImage azureVirtualMachineImage) {
        String id = convertToImageSummaryId(azureVirtualMachineImage);
        String name = convertToImageSummaryName(azureVirtualMachineImage);
        return new ImageSummary(id, name);
    }

    // TODO(chico) - review this magic numbers
    public static AzureVirtualMachineImage buildAzureVirtualMachineImageBy(String imageSummaryId) {
        String[] imageSummaryIdChunks = imageSummaryId.split(IMAGE_SUMMARY_ID_SEPARETOR);
        String published = imageSummaryIdChunks[0];
        String offer = imageSummaryIdChunks[1];
        String sku = imageSummaryIdChunks[2];
        return new AzureVirtualMachineImage(published, offer, sku);
    }

    private static String convertToImageSummaryId(AzureVirtualMachineImage azureVirtualMachineImage) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder
                .append(azureVirtualMachineImage.getPublisher())
                .append(IMAGE_SUMMARY_ID_SEPARETOR)
                .append(azureVirtualMachineImage.getOffer())
                .append(IMAGE_SUMMARY_ID_SEPARETOR)
                .append(azureVirtualMachineImage.getSku())
                .toString();
    }

    private static String convertToImageSummaryName(AzureVirtualMachineImage azureVirtualMachineImage) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder
                .append(azureVirtualMachineImage.getOffer())
                .append(IMAGE_SUMMARY_NAME_SEPARETOR)
                .append(azureVirtualMachineImage.getSku())
                .toString();
    }

    public static List<AzureVirtualMachineImage> recoverImages(Azure azure, Region region) {
        List<String> allowedPublishersNames = Arrays.asList("Canonical", "CoreOS");
        List<AzureVirtualMachineImage> azureVirtualMachineImages = new ArrayList<>();
        PagedList<VirtualMachinePublisher> imagePublishersByRegion = ImageApi.getImagePublishersByRegion(azure, region);
        imagePublishersByRegion.stream()
                .filter(publisher -> allowedPublishersNames.contains(publisher.name()))
                .forEach(publisher -> {
                    PagedList<VirtualMachineOffer> virtualMachineOffers = publisher.offers().list();
                    virtualMachineOffers.stream()
                            .forEach(offer -> {
                                PagedList<VirtualMachineSku> virtualMachineSkus = offer.skus().list();
                                virtualMachineSkus.stream()
                                        .forEach(sku -> {
                                            AzureVirtualMachineImage azureVirtualMachineImage =
                                                    new AzureVirtualMachineImage(publisher.name(), offer.name(), sku.name());
                                            azureVirtualMachineImages.add(azureVirtualMachineImage);
                                            System.out.println(azureVirtualMachineImage.toString());
                                        });
                            });
                });

        return azureVirtualMachineImages;
    }
}
