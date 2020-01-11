package com.fgan.azure.fogbowmock.image;

import cloud.fogbow.ras.api.http.response.ImageSummary;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import org.apache.commons.lang3.StringUtils;

public class AzureImageOperationUtil {

    // TODO(chico) - change visibility when it goes to the Fogbow code.
    public static final String IMAGE_SUMMARY_ID_SEPARETOR = "@#";
    static final String IMAGE_SUMMARY_NAME_SEPARETOR = " - ";

    private static final int SUMMARY_ID_PARAMETER_SIZE = 3;
    private static final int PUBLISHER_ID_SUMMARY_POSITION = 0;
    private static final int OFFER_ID_SUMMARY_POSITION = 1;
    private static final int SKU_ID_SUMMARY_POSITION = 2;

    private static final int SUMMARY_NAME_PARAMETER_SIZE = 2;
    private static final int OFFER_NAME_SUMMARY_POSITION = 0;
    private static final int SKU_NAME_SUMMARY_POSITION = 1;

    static ImageSummary buildImageSummaryBy(AzureGetImageRef azureVirtualMachineImage) {
        String id = convertToImageSummaryId(azureVirtualMachineImage);
        String name = convertToImageSummaryName(azureVirtualMachineImage);
        return new ImageSummary(id, name);
    }

    public static AzureGetImageRef buildAzureVirtualMachineImageBy(String imageSummaryId) {
        String[] imageSummaryIdChunks = imageSummaryId.split(IMAGE_SUMMARY_ID_SEPARETOR);
        String published = imageSummaryIdChunks[PUBLISHER_ID_SUMMARY_POSITION];
        String offer = imageSummaryIdChunks[OFFER_ID_SUMMARY_POSITION];
        String sku = imageSummaryIdChunks[SKU_ID_SUMMARY_POSITION];
        return new AzureGetImageRef(published, offer, sku);
    }

    static String convertToImageSummaryId(AzureGetImageRef azureVirtualMachineImage) {
        String[] list = new String[SUMMARY_ID_PARAMETER_SIZE];
        list[PUBLISHER_ID_SUMMARY_POSITION] = azureVirtualMachineImage.getPublisher();
        list[OFFER_ID_SUMMARY_POSITION] = azureVirtualMachineImage.getOffer();
        list[SKU_ID_SUMMARY_POSITION] = azureVirtualMachineImage.getSku();
        return StringUtils.join(list, IMAGE_SUMMARY_ID_SEPARETOR);
    }

    static String convertToImageSummaryName(AzureGetImageRef azureVirtualMachineImage) {
        String[] list = new String[SUMMARY_NAME_PARAMETER_SIZE];
        list[OFFER_NAME_SUMMARY_POSITION] = azureVirtualMachineImage.getOffer();
        list[SKU_NAME_SUMMARY_POSITION] = azureVirtualMachineImage.getSku();
        return StringUtils.join(list, IMAGE_SUMMARY_NAME_SEPARETOR);
    }

}
