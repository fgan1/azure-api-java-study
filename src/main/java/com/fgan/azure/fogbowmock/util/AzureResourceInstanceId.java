package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.ras.core.models.orders.Order;

public class AzureResourceInstanceId {

    /**
     * Generate the Azure Resource Name. The Azure resource name is equals to Fogbow instance Id
     * in order to main a pattern in the Fogbow context. Also, the resource name is used to
     * generate the real Azure Resource ID thus It is important to the all plugins operations.
     */
    public static String generateAzureResourceNameBy(Order order) {
        return generateFogbowInstanceIdBy(order);
    }

    /**
     * Generate the Fogbow Instance Id. The Fogbow Instance Id does not to be the Azure Resource Id
     * due to the fact that the Azure Resource Id is a URL thus It would annoying the Fogbow Rest api.
     */
    public static String generateFogbowInstanceIdBy(Order order) {
        return order.getId();
    }

    public static String generateAzureResourceNameBy(String fogbowIntanceId) {
        return fogbowIntanceId;
    }

}
