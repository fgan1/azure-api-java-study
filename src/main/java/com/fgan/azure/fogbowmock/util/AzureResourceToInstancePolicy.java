package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.sun.istack.internal.Nullable;

import java.util.function.BiFunction;

public class AzureResourceToInstancePolicy {

    /**
     * Generate the Azure Resource Name and check if It's in accordance with the policy.
     */
    private static String generateAzureResourceNameBy(@Nullable String orderName, String orderId,
                                                      AzureCloudUser azureCloudUser)
            throws InvalidParameterException {

        if (orderName == null) {
            orderName = SystemConstants.FOGBOW_INSTANCE_NAME_PREFIX + orderId;
        }

        AzureIdBuilder.configure(azureCloudUser).checkIdSizePolicy(orderName);
        return orderName;
    }

    public static String generateAzureResourceNameBy(
            ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws InvalidParameterException {

        return generateAzureResourceNameBy(computeOrder.getName(), computeOrder.getId(), azureCloudUser);
    }

    public static String generateAzureResourceNameBy(String orderId, AzureCloudUser azureCloudUser)
            throws InvalidParameterException {

        return generateAzureResourceNameBy(null, orderId, azureCloudUser);
    }

    public static String generateFogbowInstanceIdBy(String orderId, AzureCloudUser azureCloudUser,
                                                    BiFunction<String, AzureCloudUser, String> builderId)
            throws InvalidParameterException {

        String resourceName = generateAzureResourceNameBy(orderId, azureCloudUser);
        return generateFogbowIstanceId(resourceName, azureCloudUser, builderId);
    }

    public static String generateFogbowInstanceIdBy(ComputeOrder order,
                                                    AzureCloudUser azureCloudUser,
                                                    BiFunction<String, AzureCloudUser, String> builderId)
            throws InvalidParameterException {

        String resourceName = generateAzureResourceNameBy(order, azureCloudUser);
        return generateFogbowIstanceId(resourceName, azureCloudUser, builderId);
    }

    private static String generateFogbowIstanceId(String resourceName,
                                                  AzureCloudUser azureCloudUser,
                                                  BiFunction<String, AzureCloudUser, String> builderId) {
        return builderId.apply(resourceName, azureCloudUser);
    }

}
