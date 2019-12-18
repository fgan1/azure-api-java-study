package com.fgan.azure.api;

import com.fgan.azure.Constants;
import com.fgan.azure.util.GeneralPrintUtil;
import com.fgan.azure.util.PropertiesUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;

/**
 * Samples Disk operation:
 * - https://azure.microsoft.com/en-us/blog/java-manage-azure-managed-disks/
 * - https://github.com/Azure-Samples/managed-disk-java-convert-existing-virtual-machines-to-use-managed-disks
 * - https://github.com/Azure-Samples/managed-disk-java-create-virtual-machine-using-custom-image
 *
 * Fogbow Context:
 * Disk is a Volume
 */
public class VolumeApi {

    public static final String DISK_NAME_DEFAULT = Constants.PREFIX + "disk";
    public static final int DISK_SIZE_DEFAULT = 5;

    public static void printMostInformation(Azure azure) {
        PagedList<Disk> disks = getDisks(azure);
        GeneralPrintUtil.printDisksLines(disks);
    }

    public static void deleteDiskByVirtualMachine(Azure azure, String virtualMachineId) {
        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);
        String diskId = virtualMachine.osDiskId();
        deleteDisk(azure, diskId);
    }

    /**
     * Create disk not attached to any Virtual Machine.
     *
     * Even though It is a synchronous operation, It does not spends a lot of time.
     */
    private static Disk createDiskSync(Azure azure, String resourceGroupName) {
        return azure.disks().define(DISK_NAME_DEFAULT)
                .withRegion(Constants.REGION_DEFAULT)
                .withExistingResourceGroup(resourceGroupName)
                .withData()
                .withSizeInGB(DISK_SIZE_DEFAULT)
                .create();
    }

    public static Disk createDiskSync(Azure azure) {
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();
        return createDiskSync(azure, resourceGroupName);
    }

    private static PagedList<Disk> getDisks(Azure azure) {
        return azure.disks().list();
    }

    public static Disk getDisk(Azure azure, String diskId) {
        return azure.disks().getById(diskId);
    }

    public static void deleteDisk(Azure azure, String diskId) {
        azure.disks().deleteById(diskId);
    }

}
