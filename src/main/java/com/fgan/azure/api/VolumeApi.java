package com.fgan.azure.api;

import com.fgan.azure.util.GeneralPrintUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;

/**
 * Samples Disk operation:
 * - https://github.com/Azure-Samples/managed-disk-java-convert-existing-virtual-machines-to-use-managed-disks
 * - https://github.com/Azure-Samples/managed-disk-java-create-virtual-machine-using-custom-image
 */
public class VolumeApi {

    public static void printMostInformation(Azure azure) {
        PagedList<Disk> disks = getDisks(azure);
        GeneralPrintUtil.printDisksLines(disks);
    }

    public static void deleteDiskByVirtualMachine(Azure azure, String virtualMachineId) {
        VirtualMachine virtualMachine = ComputeApi.getVirtualMachine(azure, virtualMachineId);
        String diskId = virtualMachine.osDiskId();
        deleteDisk(azure, diskId);
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
