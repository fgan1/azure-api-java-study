package com.fgan.azure.api;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.AvailabilitySet;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

public class Compute {

    /**
     * Create Virtual Machine.
     */
    public static void runSambleOne(Azure azure) throws Exception {
        NetworkInterface networkInterface = getNetworkInterface(azure);
        AvailabilitySet availabilitySet = getAvailabilitySet(azure);
        createVirtualMachine(azure, networkInterface, availabilitySet);
    }

    private static AvailabilitySet getAvailabilitySet(Azure azure) {
        return null;
    }

    private static NetworkInterface getNetworkInterface(Azure azure) {
        return null;
    }

    private static VirtualMachine createVirtualMachine(Azure azure,
                                                      NetworkInterface networkInterface,
                                                      AvailabilitySet availabilitySet) {
        return azure.virtualMachines()
                .define("myVM")
                .withRegion(Region.US_EAST)
                .withExistingResourceGroup("myResourceGroup")
                .withExistingPrimaryNetworkInterface(networkInterface)
                .withLatestLinuxImage("", "", "")
                .withRootUsername("chico")
                .withRootPassword("chico")
                .withComputerName("myVM")
                .withExistingAvailabilitySet(availabilitySet)
                .withSize("Standard_DS1")
                .create();
    }

}
