package com.fgan.azure;

import com.fgan.azure.api.*;
import com.fgan.azure.util.AzureIDBuilder;
import com.fgan.azure.util.GeneralPrintUtil;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;

import javax.naming.ServiceUnavailableException;

public class SampleExecution {

    private static SampleExecution instance = new SampleExecution();

    public static SampleExecution start() {
        System.out.println("||||||||||||| Starting Test |||||||||||||");
        return instance;
    }

    public SampleExecution finish() {
        System.out.println("||||||||||||| Ending Test |||||||||||||");
        return this.instance;
    }

    // NETWORK test
    public SampleExecution printNetworkInformation(Azure azure) {
        NetworkApi.printInformation(azure);
        return this.instance;
    }

    public SampleExecution createNetworkStyleFogbow(Azure azure) {
        NetworkApi.createNetworkAsync(azure);
        return this.instance;
    }

    public SampleExecution deleteNetworkStyleFogbow(Azure azure) {
        NetworkApi.deleteNetworkAsync(azure);
        return this.instance;
    }

    // COMPUTE tests
    public SampleExecution runComputeSampleOne(Azure azure) {
        ComputeApi.runSambleOneSync(azure);
        return this.instance;
    }

    public SampleExecution createComputeStyleFogbow(Azure azure) throws InterruptedException {
        ComputeApi.createComputeFogbowWithObservebla(azure);
        return this.instance;
    }

    public SampleExecution printComputeInformation(Azure azure) {
        ComputeApi.printMostInformation(azure);
        return this.instance;
    }

    public SampleExecution printVirtualMachineAlreadyCreatedInformation(Azure azure, String id)
            throws Exception {

        ComputeApi.printVmInformation(azure, id);
        return this.instance;
    }

    public SampleExecution deleteVirtualMachineAlreadyCreated(Azure azure, String id)
            throws Exception {

        ComputeApi.deleteVmSync(azure, id);
        return this.instance;
    }

    public SampleExecution deleteVirtualMachineAlreadyCreatedByNameAsync(Azure azure, String name)
            throws Exception {

        String virtualMachineId = AzureIDBuilder.buildVirtualMachineId(name);
        ComputeApi.deleteVmAsync(azure, virtualMachineId);
        return this.instance;
    }

    public SampleExecution deleteVirtualMachineAlreadyCreatedByIdAsync(Azure azure, String id)
            throws Exception {

        ComputeApi.deleteVmAsync(azure, id);
        return this.instance;
    }

    //    ComputeApi.printInformation(azure);

    // Volume
    public SampleExecution printVolumeInformation(Azure azure) {
        VolumeApi.printMostInformation(azure);
        return this.instance;
    }

    public SampleExecution printDiskCreatedInformation(Azure azure, String id) {
        Disk disk = VolumeApi.getDisk(azure, id);
        GeneralPrintUtil.printLines(disk::name, disk::id);
        return this.instance;
    }

    public SampleExecution deleteDiskCreatedByName(Azure azure, String name) {
        String diskId = AzureIDBuilder.buildDiskId(name);
        VolumeApi.deleteDisk(azure, diskId);
        return this.instance;
    }

    public SampleExecution deleteDiskCreatedById(Azure azure, String diskId) {
        VolumeApi.deleteDisk(azure, diskId);
        return this.instance;
    }

    public SampleExecution deleteDiskByVirtualMachine(Azure azure, String virtualMachineId) {
        VolumeApi.deleteDiskByVirtualMachine(azure, virtualMachineId);
        return this.instance;
    }

    public SampleExecution createDiskSync(Azure azure) {
        VolumeApi.createDiskSync(azure);
        return this.instance;
    }

    // Security Rules
    public SampleExecution printSecurityRulesFromDefaultSecurityGroup(Azure azure) {
        SecurityRuleApi.printSecurityRulesFromDefaultSecurityGroup(azure);
        return this.instance;
    }

    public SampleExecution addSecurityRules(Azure azure) {
        SecurityRuleApi.addSecurityRules(azure);
        return this.instance;
    }

    // Attachment
    public SampleExecution detackDiskByNameFromDefaultVmSync(Azure azure, String id) {
        AttachmentApi.detackDiskByNameFromDefaultVmSync(azure, id);
        return this.instance;
    }

    public SampleExecution attackDiskByNameFromDefaultVmSync(Azure azure, String id) {
        AttachmentApi.attackDiskByNameFromDefaultVmSync(azure, id);
        return this.instance;
    }

    // Identity
    public SampleExecution checkAuthenticationByRequest() {
        IdentityApi.checkAuthenticationByRequest();
        return this.instance;
    }

    public SampleExecution checkAuthenticationStyleFogbow() throws ServiceUnavailableException {
        IdentityApi.checkAuthentication();
        return this.instance;
    }

    // TODO(chico) - finish refactoring
//    ImageApi.printInformation(azure);
//    ComputeApi.getNetworkIntefaces(azure);

}
