package com.fgan.azure;

public class AzureIDBuilder {

    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/virtualMachines/{virtualMachineName}
    private static String VIRTUAL_MACHINE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/disks/{diskName}
    private static String DISK_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/disks/%s";

    public static String buildVirtualMachineId(String name) {
        return buildId(VIRTUAL_MACHINE_STRUCTURE, name);
    }

    public static String buildDiskId(String name) {
        return buildId(DISK_STRUCTURE, name);
    }

    private static String buildId(String structure, String name) {
        String subscription = PropertiesHolder.getSubscriptionProp();
        String resourceGroupName = PropertiesHolder.getResourceGroupNameProp();
        return String.format(structure, subscription, resourceGroupName, name);
    }

}
