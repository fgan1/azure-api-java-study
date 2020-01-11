package com.fgan.azure.fogbowmock.image;

import cloud.fogbow.common.exceptions.UnexpectedException;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

public class AzureVirtualMachineImageSDK {

    public static PagedList<VirtualMachinePublisher> getImagePublishersByRegion(Azure azure, Region region)
            throws UnexpectedException {

        try {
            return azure.virtualMachineImages().publishers().listByRegion(region);
        } catch (RuntimeException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
    }

}
