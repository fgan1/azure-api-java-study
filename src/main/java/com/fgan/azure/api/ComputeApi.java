package com.fgan.azure.api;

import cloud.fogbow.common.exceptions.UnexpectedException;
import com.fgan.azure.Constants;
import com.fgan.azure.api.network.NetworkApi;
import com.fgan.azure.util.AzureIDBuilderGeneral;
import com.fgan.azure.util.GeneralPrintUtil;
import com.fgan.azure.util.PropertiesUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.AvailabilitySet;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.compute.VirtualMachineSizes;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Completable;
import rx.Observable;

public class ComputeApi {

    public static final String VM_NAME_DEFAULT = Constants.PREFIX + "virtual-machine";
    private static final String OS_USER_NAME_DEFAULT = "azure";
    private static final String OS_USER_PASSWORD_DEFAULT = "4zUre-";

    // This VirtualMachine size is available to free tier access
    private static final String VIRTUAL_MACHINE_SIZE_FREE_TIER = "Standard_B1s";
    // Publisher returned in the AZURE CLI fetch
    private static final String IMAGE_PUBLISHER_DEFAULT = "Canonical";
    private static final String IMAGE_OFFER_DEFAULT = "UbuntuServer";
    private static final String IMAGE_SKU_DEFAULT = "18.04-LTS";
    private static final int DISK_SIZE_DEFAULT = 15;

    /**
     * Create Virtual Machine.
     * Sample to firts tests.
     */
    public static void runSambleOneSync(Azure azure) throws Exception {
        String networkInterfaceId = PropertiesUtil.getNetworkInterfaceIdProp();
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();

        NetworkInterface networkInterface = new NetworkApi(azure).getNetworkInterface(azure, networkInterfaceId);
        String userData = PropertiesUtil.getUserData();
        ResourceGroup resourceGroup = ManagerApi.getResourceGroup(azure, resourceGroupName);
        VirtualMachine virtualMachine = createVirtualMachineSync(azure, networkInterface, userData, resourceGroup.name());
        GeneralPrintUtil.printLines(virtualMachine::id,
                virtualMachine::vmId,
                virtualMachine::computerName,
                virtualMachine::size);
    }

    /**
     * Create compute asynchronously.
     * Reactive Programming is applied.
     * <p>
     * When Running:
     * - States (powerState / provisioningState):
     * -- (1) PowerState/starting|Creating
     * -- (2) PowerState/running|Creating
     * -- (3) PowerState/running|Succeeded
     */
    public static void createComputeFogbowWithObservebla(Azure azure) throws Exception {
        String networkInterfaceId = PropertiesUtil.getNetworkInterfaceIdProp();
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();

        NetworkInterface networkInterface = new NetworkApi(azure).getNetworkInterface(azure, networkInterfaceId);
        String userData = PropertiesUtil.getUserData();
        ResourceGroup resourceGroup = ManagerApi.getResourceGroup(azure, resourceGroupName);

        Observable<Indexable> virtualMachineAsync =
                createVirtualMachineAsync(azure, networkInterface, userData, resourceGroup);
        virtualMachineAsync.subscribe(v -> {
            System.out.println("Index" + v.key().toString());
        }, err -> {
            err.printStackTrace();
        }, () -> {
            System.out.println("Completed.");
        });

        String id = AzureIDBuilderGeneral.buildVirtualMachineId(ComputeApi.VM_NAME_DEFAULT);
        verifyVMState(azure, id);
    }

    private static void verifyVMState(Azure azure, String virtualMachineId) throws InterruptedException {
        final int SLEEP_TIME = 10000;
        int count = 0;
        while (count < 16) {
            try {
                VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);
                System.out.println("VirtualMachine state:");
                GeneralPrintUtil.printLines(virtualMachine::name, virtualMachine::powerState, virtualMachine::provisioningState);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(SLEEP_TIME);
            count++;
        }
    }

    public static void deleteVmSync(Azure azure, String virtualMachineId) {
        deleteVirtualMachine(azure, virtualMachineId);
    }

    /**
     * Delete VM; This operation is asynchronous.
     * <p>
     * Fogbow Steps:
     * - Delete VirtualMachine
     * - Delete Disk
     */
    public static void deleteVmAsync(Azure azure, String virtualMachineId) throws Exception {
        VirtualMachine virtualMachine = getVirtualMachineById(azure, virtualMachineId);
        String osDiskId = virtualMachine.osDiskId();
        Completable completable = deleteVirtualMachineAsync(azure, virtualMachineId);
        completable.subscribe(() -> {
            System.out.println(String.format("Complete and Deleting disk %s", osDiskId));
            VolumeApi.deleteDisk(azure, osDiskId);
        }, (err) -> {
            err.printStackTrace();
        });
    }

    public static void printVmInformation(Azure azure, String virtualMachineId) throws Exception {
        VirtualMachine virtualMachine = getVirtualMachineById(azure, virtualMachineId);
        GeneralPrintUtil.printLines(virtualMachine::name,
                virtualMachine::id,
                virtualMachine::powerState,
                virtualMachine::provisioningState,
                virtualMachine::computerName,
                virtualMachine::size);
    }

    public static void printMostInformation(Azure azure) {
        PagedList<VirtualMachine> virtualMachines = getVirtualMachines(azure);
        GeneralPrintUtil.printVirtualMachinesLines(virtualMachines);

        PagedList<VirtualMachineSize> virtualMachineSizes = getVirtualMachineSizes(azure);
        GeneralPrintUtil.printVirtualMachineSizeLines(virtualMachineSizes);
    }

    private static PagedList<NetworkInterface> getNetworkIntefaces(Azure azure) {
        return azure.networkInterfaces().list();
    }

    /**
     * Get all Virtual Machine Sizes
     * <p>
     * It means in Fogbow context :
     * Virtual Machine Size is the flavor
     * Amount of vCPU and Memory
     */
    public static PagedList<VirtualMachineSize> getVirtualMachineSizes(Azure azure) {
        Region region = Constants.REGION_DEFAULT;
        return getVirtualMachineSizes(azure, region);
    }

    public static PagedList<VirtualMachineSize> getVirtualMachineSizes(Azure azure, Region region) {
        VirtualMachineSizes sizes = azure.virtualMachines().sizes();
        return sizes.listByRegion(region);
    }

    private static AvailabilitySet getAvailabilitySet(Azure azure, String id) {
        return azure.availabilitySets().getById(id);
    }

    public static VirtualMachine getVirtualMachine(Azure azure) {
        return getVirtualMachineByName(azure, VM_NAME_DEFAULT);
    }

    public static VirtualMachine getVirtualMachineByName(Azure azure, String virtualMachineName) {
        String virtualMachineId = AzureIDBuilderGeneral.buildVirtualMachineId(virtualMachineName);
        return azure.virtualMachines().getById(virtualMachineId);
    }

    public static VirtualMachine getVirtualMachineById(Azure azure, String virtualMachineId) throws UnexpectedException {
        try {
            return azure.virtualMachines().getById(virtualMachineId);
        } catch (RuntimeException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
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

    public static Completable deleteVirtualMachineAsync(Azure azure, String virtualMachineId) {
        return azure.virtualMachines().deleteByIdAsync(virtualMachineId);
    }

    private static Observable<Indexable> createVirtualMachineAsync(Azure azure,
                                                                   NetworkInterface networkInterface,
                                                                   String userData,
                                                                   ResourceGroup resourceGroup) {

        VirtualMachine.DefinitionStages.WithCreate virtualMachineContextCreation =
                createVirtualMachineContextCreation(azure, networkInterface, userData, resourceGroup.name());
        return virtualMachineContextCreation.createAsync();
    }

    public static Observable<Indexable> createVirtualMachineAsync(
            Azure azure, String virtualMachineName, Region region,
            String resourceGroupName, NetworkInterface networkInterface,
            String imagePublished, String imageOffer, String imageSku,
            String osUserName, String osUserPassword, String osComputeName,
            String userData, int diskSize, String size) {

        VirtualMachine.DefinitionStages.WithCreate virtualMachineContextCreation =
                createVirtualMachineContextCreation(azure, VM_NAME_DEFAULT, Constants.REGION_DEFAULT,
                        resourceGroupName, networkInterface, IMAGE_PUBLISHER_DEFAULT, IMAGE_OFFER_DEFAULT,
                        IMAGE_SKU_DEFAULT, OS_USER_NAME_DEFAULT, OS_USER_PASSWORD_DEFAULT, VM_NAME_DEFAULT,
                        userData, DISK_SIZE_DEFAULT, VIRTUAL_MACHINE_SIZE_FREE_TIER);
        return virtualMachineContextCreation.createAsync();
    }

    /**
     * Create virtual machine by synchronous operation.
     * Notes: It might spend minutes
     */
    public static VirtualMachine createVirtualMachineSync(Azure azure,
                                                          NetworkInterface networkInterface,
                                                          String userData,
                                                          String resourceGroupName) {

        VirtualMachine.DefinitionStages.WithCreate virtualMachineContextCreation =
                createVirtualMachineContextCreation(azure, networkInterface, userData, resourceGroupName);
        return virtualMachineContextCreation.create();
    }

    public static VirtualMachine createVirtualMachineSync(
            Azure azure, String virtualMachineName, Region region,
            String resourceGroupName, NetworkInterface networkInterface,
            String imagePublished, String imageOffer, String imageSku,
            String osUserName, String osUserPassword, String osComputeName,
            String userData, int diskSize, String size) {

        VirtualMachine.DefinitionStages.WithCreate virtualMachineContextCreation =
                createVirtualMachineContextCreation(azure, VM_NAME_DEFAULT, Constants.REGION_DEFAULT,
                        resourceGroupName, networkInterface, IMAGE_PUBLISHER_DEFAULT, IMAGE_OFFER_DEFAULT,
                        IMAGE_SKU_DEFAULT, OS_USER_NAME_DEFAULT, OS_USER_PASSWORD_DEFAULT, VM_NAME_DEFAULT,
                        userData, DISK_SIZE_DEFAULT, VIRTUAL_MACHINE_SIZE_FREE_TIER);
        return virtualMachineContextCreation.create();
    }

    public static VirtualMachine.DefinitionStages.WithCreate createVirtualMachineContextCreation(
            Azure azure,
            NetworkInterface networkInterface,
            String userData,
            String resourceGroupName) {

        return createVirtualMachineContextCreation(azure, VM_NAME_DEFAULT, Constants.REGION_DEFAULT,
                resourceGroupName, networkInterface, IMAGE_PUBLISHER_DEFAULT, IMAGE_OFFER_DEFAULT, IMAGE_SKU_DEFAULT,
                OS_USER_NAME_DEFAULT, OS_USER_PASSWORD_DEFAULT, VM_NAME_DEFAULT, userData, DISK_SIZE_DEFAULT, VIRTUAL_MACHINE_SIZE_FREE_TIER);
    }

    public static VirtualMachine.DefinitionStages.WithCreate createVirtualMachineContextCreation(
            Azure azure, String virtualMachineName, Region region,
            String resourceGroupName, NetworkInterface networkInterface,
            String imagePublished, String imageOffer, String imageSku,
            String osUserName, String osUserPassword, String osComputeName,
            String userData, int diskSize, String size) {

        return azure.virtualMachines()
                .define(virtualMachineName)
                .withRegion(region)
                .withExistingResourceGroup(resourceGroupName)
                .withExistingPrimaryNetworkInterface(networkInterface)
                .withLatestLinuxImage(imagePublished, imageOffer, imageSku)
                .withRootUsername(osUserName)
                .withRootPassword(osUserPassword)
                .withComputerName(osComputeName)
                .withCustomData(userData)
                .withOSDiskSizeInGB(diskSize)
                .withSize(size);
    }

}
