package com.fgan.azure.fogbowmock;

public class AzureResourceNameUtil {

    protected static final String VIRTUAL_MACHINE_TYPE = "virtual-machine";

    public static String createVirtualMachineName(String orderId) {
        return createResourceName(VIRTUAL_MACHINE_TYPE, orderId);
    }

    private static String createResourceName(String type, String orderId) {
        return String.format("%s-%s", type, orderId);
    }

}
