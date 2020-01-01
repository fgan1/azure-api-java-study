package com.fgan.azure.api.image;

import com.fgan.azure.util.GeneralPrintUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;

public class ImageApiSample {

    public static void printInformation(Azure azure) {
        PagedList<VirtualMachinePublisher> imagePublishers = ImageApi.getImagePublishersByRegionDefault(azure);
        GeneralPrintUtil.printVirtualMachinePublishersLines(imagePublishers);

        PagedList<VirtualMachineImage> virtualMachineImages = ImageApi.getImagesByRegionDefault(azure);
        GeneralPrintUtil.printVirtualMachineImage(virtualMachineImages);
    }

}
