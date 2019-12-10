package com.fgan.azure.api;

import com.fgan.azure.Constants;
import com.fgan.azure.PrintHolder;
import com.fgan.azure.PropertiesHolder;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.*;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.sun.istack.internal.Nullable;

public class ComputeApi {

    private static final String VM_NAME_DEFAULT = Constants.PREFIX + "virtual-machine";
    private static final String OS_USER_NAME_DEFAULT = "azure";
    private static final String OS_USER_PASSWORD_DEFAULT = "4zUre-";
    private static final Region REGION_DEFAULT = Region.US_EAST;

    // This VirtualMachine size is available to free tier access
    private static final String VIRTUAL_MACHINE_SIZE_FREE_TIER = "Standard_B1s";
    private static final String IMAGE_PUBLISHED_DEFAULT = "Canonical";
    private static final String IMAGE_OFFER_DEFAULT = "UbuntuServer";
    private static final String IMAGE_SKU_DEFAULT = "18.04-LTS";

    /**
     * Create Virtual Machine.
     */
    public static void runSambleOne(Azure azure) {
        String networkInterfaceId = PropertiesHolder.getNetworkInterfaceIdProp();
        String resourceGroupName = PropertiesHolder.getResourceGroupNameProp();

        NetworkInterface networkInterface = NetworkApi.getNetworkInterface(azure, networkInterfaceId);
        ResourceGroup resourceGroup = getResourceGroup(azure, resourceGroupName);
        createVirtualMachine(azure, networkInterface, resourceGroup);
    }

    public static void printInformation(Azure azure) {
//        PagedList<VirtualMachineImage> vmImages = getVMImages(azure);
//        PrintHolder.printVirtualMachineImageLines(vmImages);

        PagedList<VirtualMachineSize> virtualMachineSizes = getVirtualMachineSizes(azure);
        PrintHolder.printVirtualMachineSizeLines(virtualMachineSizes);
    }

    public static PagedList<NetworkInterface> getNetworkIntefaces(Azure azure) {
        return azure.networkInterfaces().list();
    }

    public static PagedList<VirtualMachineImage> getVMImages(Azure azure) {
        return azure.virtualMachineImages().listByRegion(REGION_DEFAULT);
    }

    /**
     * Get all Virtual Machine Sizes
     *
     * Meanings:
     * Virtual Machine Size is the flavor
     * Amount of vCPU and Memory
     */
    public static PagedList<VirtualMachineSize> getVirtualMachineSizes(Azure azure) {
        VirtualMachineSizes sizes = azure.virtualMachines().sizes();
        return sizes.listByRegion(REGION_DEFAULT);
    }

    @Nullable
    private static AvailabilitySet getAvailabilitySet(Azure azure, String id) {
        return azure.availabilitySets().getById(id);
    }

    private static ResourceGroup getResourceGroup(Azure azure, String name) {
        return azure.resourceGroups().getByName(name);
    }

    private static VirtualMachine createVirtualMachine(Azure azure,
                                                       NetworkInterface networkInterface,
                                                       ResourceGroup resourceGroup) {
        return azure.virtualMachines()
                .define(VM_NAME_DEFAULT)
                .withRegion(REGION_DEFAULT)
                .withExistingResourceGroup(resourceGroup)
                .withExistingPrimaryNetworkInterface(networkInterface)
                .withLatestLinuxImage(IMAGE_PUBLISHED_DEFAULT, IMAGE_OFFER_DEFAULT, IMAGE_SKU_DEFAULT)
                .withRootUsername(OS_USER_NAME_DEFAULT)
                .withRootPassword(OS_USER_PASSWORD_DEFAULT)
                .withComputerName(VM_NAME_DEFAULT)
                .withCustomData("")
                .withSize(VIRTUAL_MACHINE_SIZE_FREE_TIER)
                .withOSDiskName("")
                .create();
    }

}
