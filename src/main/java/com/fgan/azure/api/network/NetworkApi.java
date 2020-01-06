package com.fgan.azure.api.network;

import com.fgan.azure.api.ApiAzure;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.util.PropertiesUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Completable;
import rx.Observable;

public class NetworkApi extends ApiAzure {

    public NetworkApi(Azure azure) {
        super(azure);
    }

    public static NetworkInterface getNetworkInterface(Azure azure, String networkId) throws Exception {
        try {
            return azure.networkInterfaces().getById(networkId);
        } catch (RuntimeException e) {
            throw new AzureException.ResourceNotFound(e);
        }
    }

    Network getNetwork(String networkId) {
        return this.azure.networks().getById(networkId);
    }

    /**
     * @param securityGroupName
     * @param region
     * @param defineRule
     * @param fromAddress
     * @return
     * TODO(chico) - finish implementation
     */
    Observable<Indexable> createSecurityGroup(String securityGroupName, Region region,
                                              String defineRule, String fromAddress) {
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();

        return this.azure.networkSecurityGroups().define(securityGroupName)
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

    /**
     *
     * @param networkName
     * @param region
     * @param networkResourceGroupName
     * @param addressSpace
     * @param addressPrefix
     * @param subnetName
     * @param networkSecurityGroup
     * @return
     */
    Observable<Indexable> createNetworkAsync(String networkName, Region region, String networkResourceGroupName,
                                             String addressSpace, String addressPrefix, String subnetName,
                                             NetworkSecurityGroup networkSecurityGroup) {
        return this.azure.networks().define(networkName)
                .withRegion(region)
                .withExistingResourceGroup(networkResourceGroupName)
                .withAddressSpace(addressSpace)
                .defineSubnet(subnetName)
                .withAddressPrefix(addressPrefix)
                .withExistingNetworkSecurityGroup(networkSecurityGroup).attach()
                .createAsync();
    }

    Observable<Indexable> createNetworkInterfaceAsync(String networkInterfaceName, Region region,
                                                      String resourceGroupName, String subnetName, Network networkId) {

        return this.azure.networkInterfaces().define(networkInterfaceName)
                .withRegion(region)
                .withExistingResourceGroup(resourceGroupName)
                .withExistingPrimaryNetwork(networkId)
                .withSubnet("subnet")
                .withPrimaryPrivateIPAddressDynamic()
                .createAsync();
    }

    /**
     *
     * @param securityGroupId
     * @return
     */
    Completable deleteSecurityGroupByIdAsync(String securityGroupId) {
        return this.azure.networkSecurityGroups().deleteByIdAsync(securityGroupId);
    }

    /**
     *
     * @param networkId
     * @return
     */
    Completable deleteNetworkByIdAsync(String networkId) {
        return this.azure.networks().deleteByIdAsync(networkId);
    }

    Completable deleteNetworkInterfaceByIdAsync(String networkInterfaceId) {
        return this.azure.networkInterfaces().deleteByIdAsync(networkInterfaceId);
    }

    /**
     *
     * @return
     */
    PagedList<Network> getNetworks() {
        return this.azure.networks().list();
    }

    /**
     *
     * @return
     */
    PagedList<NetworkInterface> getNetworkInterfaces() {
        return this.azure.networkInterfaces().list();
    }

}
