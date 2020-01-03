package com.fgan.azure.fogbowmock.image;

import cloud.fogbow.ras.api.http.response.ImageSummary;
import com.fgan.azure.api.image.ImageApi;
import com.fgan.azure.fogbowmock.AzureVirtualMachineImage;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineOffer;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.compute.VirtualMachineSku;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AzureImageOperation {

    // TODO(chico) - change visibility when it goes to the Fogbow code.
    public static final String IMAGE_SUMMARY_ID_SEPARETOR = "@#";
    @VisibleForTesting static final String IMAGE_SUMMARY_NAME_SEPARETOR = " - ";

    private static final int SUMMARY_ID_PARAMETER_SIZE = 3;
    private static final int PUBLISHER_ID_SUMMARY_POSITION = 0;
    private static final int OFFER_ID_SUMMARY_POSITION = 1;
    private static final int SKU_ID_SUMMARY_POSITION = 2;
    private static final int SUMMARY_NAME_PARAMETER_SIZE = 2;
    private static final int OFFER_NAME_SUMMARY_POSITION = 0;
    private static final int SKU_NAME_SUMMARY_POSITION = 1;

    public static ImageSummary buildImageSummaryBy(AzureVirtualMachineImage azureVirtualMachineImage) {
        String id = convertToImageSummaryId(azureVirtualMachineImage);
        String name = convertToImageSummaryName(azureVirtualMachineImage);
        return new ImageSummary(id, name);
    }

    public static AzureVirtualMachineImage buildAzureVirtualMachineImageBy(String imageSummaryId) {
        String[] imageSummaryIdChunks = imageSummaryId.split(IMAGE_SUMMARY_ID_SEPARETOR);
        String published = imageSummaryIdChunks[PUBLISHER_ID_SUMMARY_POSITION];
        String offer = imageSummaryIdChunks[OFFER_ID_SUMMARY_POSITION];
        String sku = imageSummaryIdChunks[SKU_ID_SUMMARY_POSITION];
        return new AzureVirtualMachineImage(published, offer, sku);
    }

    private static String convertToImageSummaryId(AzureVirtualMachineImage azureVirtualMachineImage) {
        String[] list = new String[SUMMARY_ID_PARAMETER_SIZE];
        list[PUBLISHER_ID_SUMMARY_POSITION] = azureVirtualMachineImage.getPublisher();
        list[OFFER_ID_SUMMARY_POSITION] = azureVirtualMachineImage.getOffer();
        list[SKU_ID_SUMMARY_POSITION] = azureVirtualMachineImage.getSku();
        return StringUtils.join(list, IMAGE_SUMMARY_ID_SEPARETOR);
    }

    private static String convertToImageSummaryName(AzureVirtualMachineImage azureVirtualMachineImage) {
        String[] list = new String[SUMMARY_NAME_PARAMETER_SIZE];
        list[OFFER_NAME_SUMMARY_POSITION] = azureVirtualMachineImage.getOffer();
        list[SKU_NAME_SUMMARY_POSITION] = azureVirtualMachineImage.getSku();
        return StringUtils.join(list, IMAGE_SUMMARY_NAME_SEPARETOR);
    }

    // TODO(chico) - Finish
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
