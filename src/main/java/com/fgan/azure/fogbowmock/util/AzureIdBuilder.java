package com.fgan.azure.fogbowmock.util;

import com.fgan.azure.fogbowmock.common.AzureCloudUser;

public class AzureIdBuilder {

    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/virtualMachines/{virtualMachineName}
    private static String VIRTUAL_MACHINE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/networkInterfaces/{networkInterfaceName}
    private static String NETWORK_INTERFACE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/networkInterfaces/%s";

    public static AzureIdBuilderConfigured configure(AzureCloudUser azureCloudUser) {
        return new AzureIdBuilderConfigured(azureCloudUser);
    }

    public static class AzureIdBuilderConfigured {

        private AzureCloudUser azureCloudUser;

        public AzureIdBuilderConfigured(AzureCloudUser azureCloudUser) {
            this.azureCloudUser = azureCloudUser;
        }

        public String buildVirtualMachineId(String virtualMachineName) {
            return buildId(VIRTUAL_MACHINE_STRUCTURE, virtualMachineName);
        }

        public String buildNetworkInterfaceId(String networkInterfaceName) {
            return buildId(NETWORK_INTERFACE_STRUCTURE, networkInterfaceName);
        }

        private String buildId(String structure, String name) {
            String subscriptionId = this.azureCloudUser.getSubscriptionId();
            String resourceGroupName = this.azureCloudUser.getName();
            return String.format(structure, subscriptionId, resourceGroupName, name);
        }

    }
}
