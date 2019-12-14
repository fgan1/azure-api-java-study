package com.fgan.azure;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.NetworkApi;
import com.fgan.azure.api.VolumeApi;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;

public class Execution {

    private static Execution instance = new Execution();

    public static Execution start() {
        System.out.println("||||||||||||| Starting Test |||||||||||||");
        return instance;
    }

    public Execution finish() {
        System.out.println("||||||||||||| Ending Test |||||||||||||");
        return this.instance;
    }

    // NETWORK test
    public Execution printNetworkInformation(Azure azure) {
        NetworkApi.printInformation(azure);
        return this.instance;
    }

    // COMPUTE tests
    public Execution runComputeSampleOne(Azure azure) {
        ComputeApi.runSambleOne(azure);
        return this.instance;
    }

    public Execution printComputeInformation(Azure azure) {
        ComputeApi.printMostInformation(azure);
        return this.instance;
    }

    public Execution printVirtualMachineAlreadyCreatedInformation(Azure azure, String id)
            throws Exception {

        ComputeApi.printVmInformation(azure, id);
        return this.instance;
    }

    public Execution deleteVirtualMachineAlreadyCreatedInformation(Azure azure, String id)
            throws Exception {

        ComputeApi.deleteVm(azure, id);
        return this.instance;
    }

    //    ComputeApi.printInformation(azure);

    // Volume

    public Execution printVolumeInformation(Azure azure) {
        VolumeApi.printMostInformation(azure);
        return this.instance;
    }

    public Execution printDiskCreatedInformation(Azure azure, String id) {
        Disk disk = VolumeApi.getDisk(azure, id);
        PrintHolder.printLines(disk::name, disk::id);
        return this.instance;
    }

    public Execution deleteDiskCreatedInformation(Azure azure, String id) {
        VolumeApi.deleteDisk(azure, id);
        return this.instance;
    }

    // TODO(chico) - finish refactoring
//    ImageApi.printInformation(azure);
//    ComputeApi.getNetworkIntefaces(azure);

}
