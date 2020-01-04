package com.fgan.azure.fogbowmock.image;

import cloud.fogbow.ras.api.http.response.ImageSummary;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class AzureImageOperationTest {

    // test case:
    @Test
    public void testBuildImageSummaryBySuccessfully() {
        // set up
        String publisherExpected = "publisherExpected";
        String offerExpected = "offerExpected";
        String skuExpected = "skuExpected";
        AzureGetImageRef azureVirtualMachineImage = Mockito.mock(AzureGetImageRef.class);
        Mockito.when(azureVirtualMachineImage.getPublisher()).thenReturn(publisherExpected);
        Mockito.when(azureVirtualMachineImage.getOffer()).thenReturn(offerExpected);
        Mockito.when(azureVirtualMachineImage.getSku()).thenReturn(skuExpected);

        String summaryIdExpected = new StringBuilder()
                .append(publisherExpected)
                .append(AzureImageOperation.IMAGE_SUMMARY_ID_SEPARETOR)
                .append(offerExpected)
                .append(AzureImageOperation.IMAGE_SUMMARY_ID_SEPARETOR)
                .append(skuExpected)
                .toString();

        String summaryNameExpected = new StringBuilder()
                .append(offerExpected)
                .append(AzureImageOperation.IMAGE_SUMMARY_NAME_SEPARETOR)
                .append(skuExpected)
                .toString();

        // execute
        ImageSummary imageSummary = AzureImageOperation.buildImageSummaryBy(azureVirtualMachineImage);

        // verify
        Assert.assertEquals(summaryIdExpected, imageSummary.getId());
        Assert.assertEquals(summaryNameExpected, imageSummary.getName());
    }

    @Test
    public void testBuildAzureVirtualMachineImageBySuccessfully() {
        // set up
        String publisherExpected = "publisherExpected";
        String offerExpected = "offerExpected";
        String skuExpected = "skuExpected";

        String summaryIdExpected = new StringBuilder()
                .append(publisherExpected)
                .append(AzureImageOperation.IMAGE_SUMMARY_ID_SEPARETOR)
                .append(offerExpected)
                .append(AzureImageOperation.IMAGE_SUMMARY_ID_SEPARETOR)
                .append(skuExpected)
                .toString();

        // execute
        AzureGetImageRef azureVirtualMachineImage = AzureImageOperation.buildAzureVirtualMachineImageBy(summaryIdExpected);

        // verify
        Assert.assertEquals(publisherExpected, azureVirtualMachineImage.getPublisher());
        Assert.assertEquals(offerExpected, azureVirtualMachineImage.getOffer());
        Assert.assertEquals(skuExpected, azureVirtualMachineImage.getSku());
    }

}
