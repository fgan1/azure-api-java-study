package com.fgan.azure.util;

public class AzureIDBuilderGeneral {

    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/virtualMachines/{virtualMachineName}
    private static String VIRTUAL_MACHINE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/disks/{diskName}
    private static String DISK_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/disks/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/networkSecurityGroups/{networkSecurityGroupName}
    private static String NETWORK_SECURITY_GROUP_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/virtualNetworks/{virtualNetworkName}
    private static String NETWORK_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/virtualNetworks/%s";

    private static String NETWORK_INTERFACE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/virtualNetworks/%s";

    public static String buildVirtualMachineId(String name) {
        return buildId(VIRTUAL_MACHINE_STRUCTURE, name);
    }

    public static String buildSecurityGroupId(String name) {
        return buildId(NETWORK_SECURITY_GROUP_STRUCTURE, name);
    }

    public static String buildNetworkId(String name) {
        return buildId(NETWORK_STRUCTURE, name);
    }

    public static String buildNetworkInterfaceId(String name) {
        return buildId(NETWORK_INTERFACE_STRUCTURE, name);
    }

    public static String buildDiskId(String name) {
        return buildId(DISK_STRUCTURE, name);
    }

    private static String buildId(String structure, String name) {
        String subscription = PropertiesUtil.getSubscriptionProp();
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();
        return String.format(structure, subscription, resourceGroupName, name);
    }
}
