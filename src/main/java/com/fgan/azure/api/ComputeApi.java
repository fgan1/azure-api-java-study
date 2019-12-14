package com.fgan.azure.api;

import com.fgan.azure.Constants;
import com.fgan.azure.PrintHolder;
import com.fgan.azure.PropertiesHolder;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.AvailabilitySet;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.compute.VirtualMachineSizes;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.sun.istack.internal.Nullable;

public class ComputeApi {

    private static final String VM_NAME_DEFAULT = Constants.PREFIX + "virtual-machine";
    private static final String OS_USER_NAME_DEFAULT = "azure";
    private static final String OS_USER_PASSWORD_DEFAULT = "4zUre-";

    // This VirtualMachine size is available to free tier access
    private static final String VIRTUAL_MACHINE_SIZE_FREE_TIER = "Standard_B1s";
    // Publisher returned in the AZURE CLI fetch
    private static final String IMAGE_PUBLISHER_DEFAULT = "Canonical";
    private static final String IMAGE_OFFER_DEFAULT = "UbuntuServer";
    private static final String IMAGE_SKU_DEFAULT = "18.04-LTS";

    /**
     * Create Virtual Machine.
     */
    public static void runSambleOne(Azure azure) {
        String networkInterfaceId = PropertiesHolder.getNetworkInterfaceIdProp();
        String resourceGroupName = PropertiesHolder.getResourceGroupNameProp();

        NetworkInterface networkInterface = NetworkApi.getNetworkInterface(azure, networkInterfaceId);
        String userData = PropertiesHolder.getUserData();
        ResourceGroup resourceGroup = getResourceGroup(azure, resourceGroupName);
        VirtualMachine virtualMachine = createVirtualMachine(azure, networkInterface, userData, resourceGroup);
        PrintHolder.printLines(virtualMachine::id,
                virtualMachine::vmId,
                virtualMachine::computerName,
                virtualMachine::size);
    }

    public static void deleteVm(Azure azure, String virtualMachineId) {
        deleteVirtualMachine(azure, virtualMachineId);
    }

    public static void printVmInformation(Azure azure, String virtualMachineId) {
        VirtualMachine virtualMachine = getVirtualMachine(azure, virtualMachineId);
        PrintHolder.printLines(virtualMachine::id,
                virtualMachine::vmId,
                virtualMachine::computerName,
                virtualMachine::size);
    }

    public static void printMostInformation(Azure azure) {
        PagedList<VirtualMachine> virtualMachines = getVirtualMachines(azure);
        PrintHolder.printVirtualMachinesLines(virtualMachines);

        PagedList<VirtualMachineSize> virtualMachineSizes = getVirtualMachineSizes(azure);
        PrintHolder.printVirtualMachineSizeLines(virtualMachineSizes);
    }

    private static PagedList<NetworkInterface> getNetworkIntefaces(Azure azure) {
        return azure.networkInterfaces().list();
    }

    /**
     * Get all Virtual Machine Sizes
     *
     * Meanings:
     * Virtual Machine Size is the flavor
     * Amount of vCPU and Memory
     */
    private static PagedList<VirtualMachineSize> getVirtualMachineSizes(Azure azure) {
        VirtualMachineSizes sizes = azure.virtualMachines().sizes();
        return sizes.listByRegion(Constants.REGION_DEFAULT);
    }

    @Nullable
    private static AvailabilitySet getAvailabilitySet(Azure azure, String id) {
        return azure.availabilitySets().getById(id);
    }

    private static ResourceGroup getResourceGroup(Azure azure, String name) {
        return azure.resourceGroups().getByName(name);
    }

    private static VirtualMachine getVirtualMachine(Azure azure, String id) {
        return azure.virtualMachines().getById(id);
    }

    private static PagedList<VirtualMachine> getVirtualMachines(Azure azure) {
        return azure.virtualMachines().list();
    }

    /**
     * Delete virtual machine by synchronous operation.
     * Notes: It might spend minutes
     */
    private static void deleteVirtualMachine(Azure azure, String virtualMachineId) {
        azure.virtualMachines().deleteById(virtualMachineId);
    }

    /**
     * Create virtual machine by synchronous operation.
     * Notes: It might spend minutes
     */
    private static VirtualMachine createVirtualMachine(Azure azure,
                                                       NetworkInterface networkInterface,
                                                       String userData,
                                                       ResourceGroup resourceGroup) {

        return azure.virtualMachines()
                .define(VM_NAME_DEFAULT)
                .withRegion(Constants.REGION_DEFAULT)
                .withExistingResourceGroup(resourceGroup)
                .withExistingPrimaryNetworkInterface(networkInterface)
                .withLatestLinuxImage(IMAGE_PUBLISHER_DEFAULT, IMAGE_OFFER_DEFAULT, IMAGE_SKU_DEFAULT)
                .withRootUsername(OS_USER_NAME_DEFAULT)
                .withRootPassword(OS_USER_PASSWORD_DEFAULT)
                .withComputerName(VM_NAME_DEFAULT)
                .withCustomData(userData)
                .withSize(VIRTUAL_MACHINE_SIZE_FREE_TIER)
                .create();
    }

}
