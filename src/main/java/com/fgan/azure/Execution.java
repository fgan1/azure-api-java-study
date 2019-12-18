package com.fgan.azure;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.NetworkApi;
import com.fgan.azure.api.SecurityRuleApi;
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

    public Execution createNetworkStyleFogbow(Azure azure) {
        NetworkApi.createNetworkAsync(azure);
        return this.instance;
    }

    public Execution deleteNetworkStyleFogbow(Azure azure) {
        NetworkApi.deleteNetworkAsync(azure);
        return this.instance;
    }

    // COMPUTE tests
    public Execution runComputeSampleOne(Azure azure) {
        ComputeApi.runSambleOneSync(azure);
        return this.instance;
    }

    public Execution createComputeStyleFogbow(Azure azure) throws InterruptedException {
        ComputeApi.createComputeFogbowWithObservebla(azure);
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

    public Execution deleteVirtualMachineAlreadyCreated(Azure azure, String id)
            throws Exception {

        ComputeApi.deleteVmSync(azure, id);
        return this.instance;
    }

    public Execution deleteVirtualMachineAlreadyCreatedByNameAsync(Azure azure, String name)
            throws Exception {

        String virtualMachineId = AzureIDBuilder.buildVirtualMachineId(name);
        ComputeApi.deleteVmAsync(azure, virtualMachineId);
        return this.instance;
    }

    public Execution deleteVirtualMachineAlreadyCreatedByIdAsync(Azure azure, String id)
            throws Exception {

        ComputeApi.deleteVmAsync(azure, id);
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
        GeneralPrintUtil.printLines(disk::name, disk::id);
        return this.instance;
    }

    public Execution deleteDiskCreatedByName(Azure azure, String name) {
        String diskId = AzureIDBuilder.buildDiskId(name);
        VolumeApi.deleteDisk(azure, diskId);
        return this.instance;
    }

    public Execution deleteDiskCreatedById(Azure azure, String diskId) {
        VolumeApi.deleteDisk(azure, diskId);
        return this.instance;
    }

    public Execution deleteDiskByVirtualMachine(Azure azure, String virtualMachineId) {
        VolumeApi.deleteDiskByVirtualMachine(azure, virtualMachineId);
        return this.instance;
    }

    // Security Rules
    public Execution printSecurityRules(Azure azure) {
        SecurityRuleApi.printSecurityRules(azure);
        return this.instance;
    }

    public Execution addSecurityRules(Azure azure) {
        SecurityRuleApi.addSecurityRules(azure);
        return this.instance;
    }

    // TODO(chico) - finish refactoring
//    ImageApi.printInformation(azure);
//    ComputeApi.getNetworkIntefaces(azure);

}
