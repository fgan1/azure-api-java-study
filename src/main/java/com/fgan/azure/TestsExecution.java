package com.fgan.azure;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.NetworkApi;
import com.microsoft.azure.management.Azure;

public class TestsExecution {

    private static TestsExecution instance = new TestsExecution();

    public static TestsExecution run() {
        System.out.println("||||||||||||| Starting Test |||||||||||||");
        return instance;
    }

    public TestsExecution finish() {
        System.out.println("||||||||||||| Ending Test |||||||||||||");
        return this.instance;
    }

    // NETWORK test
    public TestsExecution printNetworkInformation(Azure azure) {
        NetworkApi.printInformation(azure);
        return this.instance;
    }

    // COMPUTE tests
    public TestsExecution runComputeSampleOne(Azure azure) {
        ComputeApi.runSambleOne(azure);
        return this.instance;
    }

    public TestsExecution printComputeInformation(Azure azure) {
        ComputeApi.printMostInformation(azure);
        return this.instance;
    }

    public TestsExecution printVirtualMachineAlreadyCreatedInformation(Azure azure, String id)
            throws Exception {

        ComputeApi.printVmInformation(azure, id);
        return this.instance;
    }

    public TestsExecution deleteVirtualMachineAlreadyCreatedInformation(Azure azure, String id)
            throws Exception {

        ComputeApi.deleteVm(azure, id);
        return this.instance;
    }

    // TODO(chico) - finish refactoring
//    ComputeApi.printInformation(azure);
//    ImageApi.printInformation(azure);
//    ComputeApi.getNetworkIntefaces(azure);

}
