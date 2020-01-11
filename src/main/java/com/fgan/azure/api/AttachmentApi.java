package com.fgan.azure.api;

import cloud.fogbow.common.exceptions.UnexpectedException;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.util.AzureIDBuilderGeneral;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachine;

/**
 * Samples Disk operation:
 * - https://azure.microsoft.com/en-us/blog/java-manage-azure-managed-disks/
 * - https://github.com/Azure-Samples/compute-java-manage-vm.git
 *
 * Fogbow Context:
 * To attach a disk is necessary use this Virtual Machine context and update this one.
 */
public class AttachmentApi {

    public static void attackDiskByNameFromDefaultVmSync(Azure azure, String diskName)
            throws UnexpectedException {

        String virtualMachineId = AzureIDBuilderGeneral.buildVirtualMachineId(ComputeApi.VM_NAME_DEFAULT);
        String diskId = AzureIDBuilderGeneral.buildDiskId(diskName);
        attackDiskSync(azure, diskId, virtualMachineId);
    }

    /**
     * Attach disk into a Virtual Machine
     *
     * Note:
     * This operation is synchronous and spends more than 1 minute to complete.
     */
    private static void attackDiskSync(Azure azure, String diskId, String virtualMachineId)
            throws UnexpectedException {

        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);
        Disk disk = VolumeApi.getDisk(azure, diskId);
        virtualMachine.update()
                .withExistingDataDisk(disk)
                .apply();
    }

    public static void detackDiskByNameFromDefaultVmSync(Azure azure, String diskName)
            throws UnexpectedException {

        String virtualMachineId = AzureIDBuilderGeneral.buildVirtualMachineId(ComputeApi.VM_NAME_DEFAULT);
        String diskId = AzureIDBuilderGeneral.buildDiskId(diskName);
        detackDiskSync(azure, diskId, virtualMachineId);
    }

    /**
     * Detach disk into a Virtual Machine
     *
     * Note:
     * This operation is synchronous and spends more than 30 seconds to complete.
     */
    private static void detackDiskSync(Azure azure, String diskId, String virtualMachineId)
            throws UnexpectedException {

        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);
        Disk disk = VolumeApi.getDisk(azure, diskId);
        // TODO(chico) - check the magicNumber meaning
        int magicNumber = 0;
        virtualMachine.update()
                .withoutDataDisk(magicNumber)
                .apply();
    }

}
