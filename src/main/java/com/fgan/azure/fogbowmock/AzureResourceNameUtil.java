package com.fgan.azure.fogbowmock;

import cloud.fogbow.ras.core.models.orders.Order;

public class AzureResourceNameUtil {

    protected static final String VIRTUAL_MACHINE_TYPE = "virtual-machine";

    public static String createVirtualMachineName(Order order) {
        return createResourceName(VIRTUAL_MACHINE_TYPE, order.getId());
    }

    private static String createResourceName(String type, String orderId) {
        return String.format("%s-%s", type, orderId);
    }

}
