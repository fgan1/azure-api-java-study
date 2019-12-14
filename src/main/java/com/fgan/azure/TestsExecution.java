package com.fgan.azure;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.NetworkApi;
import com.microsoft.azure.management.Azure;

public class TestsExecution {

    private static TestsExecution testsExecution = new TestsExecution();

    public static TestsExecution run() {
        System.out.println("||||||||||||| Starting Test |||||||||||||");
        return testsExecution;
    }

    public TestsExecution finish() {
        System.out.println("||||||||||||| Ending Test |||||||||||||");
        return this.testsExecution;
    }

    public TestsExecution printNetworkInformation(Azure azure) {
        NetworkApi.printInformation(azure);
        return this.testsExecution;
    }

    public TestsExecution runComputeSampleOne(Azure azure) {
        ComputeApi.runSambleOne(azure);
        return this.testsExecution;
    }

    // TODO(chico) - finish refactoring
//    ComputeApi.printInformation(azure);
//    ImageApi.printInformation(azure);
//    ComputeApi.getNetworkIntefaces(azure);

}
