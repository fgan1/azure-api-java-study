package com.fgan.azure;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.IdentityApi;
import com.fgan.azure.api.NetworkApi;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1. You must export environment variables.
 * export AZURE_AUTH_LOCATION=src/main/resources/azureauth.properties
 * export AZURE_AUTH_LOCATION=src/main/resources/general.properties
 */
public class App {
    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws Exception {
        LOGGER.info("Hello Azure!");
        Azure azure = IdentityApi.getAzure();

        Option[] options = new Option[] { Option.RUN_COMPUTE_SAMPLE_ONE };
        for (int i = 0; i < options.length; i++) {
            run(azure, options[i]);
        }
    }

    // TODO (fgan) - move to another class file
    private enum Option {
        LIST_IMAGE, GET_NETWORK_INTERFACE,
        RUN_COMPUTE_SAMPLE_ONE,
        PRINT_COMPUTE_INFORMATION, PRINT_NETWORK_INFORMATION
    }

    // TODO (fgan) - move to another class file
    private static void run(Azure azure, Option option) {
        switch (option) {
            case PRINT_NETWORK_INFORMATION:
                NetworkApi.printInformation(azure);
                break;
            case PRINT_COMPUTE_INFORMATION:
                ComputeApi.printInformation(azure);
                break;
            case LIST_IMAGE:
                PagedList<VirtualMachineImage> vmImages = ComputeApi.getVMImages(azure);
                break;
            case GET_NETWORK_INTERFACE:
                ComputeApi.getNetworkIntefaces(azure);
                break;
            case RUN_COMPUTE_SAMPLE_ONE:
                ComputeApi.runSambleOne(azure);
                break;
            default:
                System.out.println("No Option chosen");
        }
    }

}
