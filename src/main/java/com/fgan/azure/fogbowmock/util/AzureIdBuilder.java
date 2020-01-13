package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.core.models.orders.Order;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.Messages;
import com.google.common.annotations.VisibleForTesting;

public class AzureIdBuilder {

    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/virtualMachines/{virtualMachineName}
    @VisibleForTesting static String VIRTUAL_MACHINE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/networkInterfaces/{networkInterfaceName}
    @VisibleForTesting static String NETWORK_INTERFACE_STRUCTURE =
            "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/networkInterfaces/%s";
    // /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Compute/disks/{diskName}
    @VisibleForTesting static final String BIGGER_STRUCTURE = NETWORK_INTERFACE_STRUCTURE;


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

        /**
         * It checks the resource name size in relation to Database Instance Order Id Maximum Size.
         * It happens because the resource name makes up the instance Id. Also, it might happen due
         * to the fact that the user choose some values such as resourceName and resourceGroupName.
         */
        public void checkIdSizePolicy(String resourceName) throws InvalidParameterException {
            String idBuilt = buildId(BIGGER_STRUCTURE, resourceName);
            int sizeExceeded = idBuilt.length() - Order.FIELDS_MAX_SIZE;
            if (sizeExceeded > 0) {
                throw new InvalidParameterException(
                        String.format(Messages.ERROR_ID_LIMIT_SIZE_EXCEEDED, sizeExceeded));
            }
        }

        @VisibleForTesting
        String buildId(String structure, String name) {
            String subscriptionId = this.azureCloudUser.getSubscriptionId();
            String resourceGroupName = this.azureCloudUser.getResourceGroupName();
            return String.format(structure, subscriptionId, resourceGroupName, name);
        }

    }
}
