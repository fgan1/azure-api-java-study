package com.fgan.azure.fogbowmock;

import com.fgan.azure.util.PropertiesUtil;

public class AzureIDBuilderFogbow {

    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/virtualMachines/{virtualMachineName}
    private static String VIRTUAL_MACHINE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s";

    public static String buildAzureVirtualMachineId(String name) {
        return buildId(VIRTUAL_MACHINE_STRUCTURE, name);
    }

    private static String buildId(String structure, String name) {
        String subscription = PropertiesUtil.getSubscriptionProp();
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();
        return String.format(structure, subscription, resourceGroupName, name);
    }
}
