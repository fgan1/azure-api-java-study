package com.fgan.azure.api;

import com.fgan.azure.PrintHolder;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;

public class VolumeApi {

    public static void printMostInformation(Azure azure) {
        PagedList<Disk> disks = getDisks(azure);
        PrintHolder.printDisksLines(disks);
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
