package com.fgan.azure.fogbowmock.network;

import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Observable;

public class AzureNetworkSDK {

    public static NetworkInterface getNetworkInterface(Azure azure, String azureNetworkInterfaceId)
            throws InstanceNotFoundException {

        try {
            return azure.networkInterfaces().getById(azureNetworkInterfaceId);
        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(e.getMessage(), e);
        }
    }

    public static Observable<Indexable> createSecurityGroup(Azure azure, String resourceGroupName,
                                                            String securityGroupName, Region region,
                                                            String defineRule, String fromAddress) {

        return azure.networkSecurityGroups()
                .define(securityGroupName)
                .withRegion(region)
                .withExistingResourceGroup(resourceGroupName)
                .defineRule(defineRule)
                    .denyInbound()
                    .fromAddress(fromAddress)
                    .fromAnyPort()
                    .toAnyAddress()
                    .toAnyPort()
                    .withAnyProtocol()
                    .attach()
                .createAsync();
    }

    static Observable<Indexable> createNetworkAsync(Azure azure, String networkName, Region region, String networkResourceGroupName,
                                                    String addressSpace, String addressPrefix, String subnetName,
                                                    NetworkSecurityGroup networkSecurityGroup) {
        return azure.networks()
                .define(networkName)
                .withRegion(region)
                .withExistingResourceGroup(networkResourceGroupName)
                .withAddressSpace(addressSpace)
                .defineSubnet(subnetName)
                    .withAddressPrefix(addressPrefix)
                    .withExistingNetworkSecurityGroup(networkSecurityGroup)
                    .attach()
                .createAsync();
    }

    static Observable<Indexable> createNetworkInterfaceAsync(Azure azure, String networkInterfaceName, Region region,
                                                      String resourceGroupName, String subnetName, Network networkId) {

        return azure.networkInterfaces().define(networkInterfaceName)
                .withRegion(region)
                .withExistingResourceGroup(resourceGroupName)
                .withExistingPrimaryNetwork(networkId)
                .withSubnet("")
                .withPrimaryPrivateIPAddressDynamic()
                .createAsync();
    }

}
