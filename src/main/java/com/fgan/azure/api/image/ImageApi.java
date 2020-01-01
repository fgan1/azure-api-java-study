package com.fgan.azure.api.image;

import com.fgan.azure.Constants;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

public class ImageApi {

    public static PagedList<VirtualMachinePublisher> getImagePublishersByRegionDefault(Azure azure) {
        return getImagePublishersByRegion(azure, Constants.REGION_DEFAULT);
    }

    public static PagedList<VirtualMachinePublisher> getImagePublishersByRegion(Azure azure, Region region) {
        return azure.virtualMachineImages().publishers().listByRegion(region);
    }

    /**
     * Get all images given a region.
     *
     * This is a synchronous operation but the data comes in chunks. Therefore, this operation
     * takes a long time. The last test it takes more the 2 minutes.
     */
    public static PagedList<VirtualMachineImage> getImagesByRegionDefault(Azure azure) {
        return azure.virtualMachineImages().listByRegion(Constants.REGION_DEFAULT);
    }

}
