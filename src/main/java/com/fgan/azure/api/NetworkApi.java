package com.fgan.azure.api;

import com.fgan.azure.AzureIDBuilder;
import com.fgan.azure.Constants;
import com.fgan.azure.GeneralPrintUtil;
import com.fgan.azure.PropertiesUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Completable;
import rx.Observable;
import rx.functions.Func1;

/**
 * Samples Network operation:
 * - https://github.com/Azure-Samples/network-java-manage-network-interface
 * - https://github.com/Azure-Samples/network-java-manage-virtual-network
 * - https://github.com/Azure-Samples/network-java-manage-virtual-network-async
 */
public class NetworkApi {

    public static final String SECURITY_GROUP_NAME_DEFAULT = Constants.PREFIX + "security-group";
    public static final String NETWORK_NAME_DEFAULT = Constants.PREFIX + "network";
    public static final String SUBNET_NAME_DEFAULT = Constants.PREFIX + "subnet";

    public static void printInformation(Azure azure) {
        PagedList<Network> networks = getNetworks(azure);
        GeneralPrintUtil.printNetworksLines(networks);

        PagedList<NetworkInterface> networkInterfaces = getNetworkInterfaces(azure);
        GeneralPrintUtil.printNetworkInterfacessLines(networkInterfaces);
    }

    public static void printSpecificNetworkInformationById(Azure azure, String networkId) {
        Network network = getNetwork(azure, networkId);
        GeneralPrintUtil.printLines(network::name, network::regionName, network::id);
    }

    public static void printSpecificNetworkInformationByName(Azure azure, String networkName) {
        String networkId = AzureIDBuilder.buildNetworkId(networkName);
        printSpecificNetworkInformationById(azure, networkId);
    }

    public static NetworkInterface getNetworkInterface(Azure azure, String networkId) {
        return azure.networkInterfaces().getById(networkId);
    }

    private static Network getNetwork(Azure azure, String networkId) {
        return azure.networks().getById(networkId);
    }

    public static void deleteNetworkAsync(Azure azure) {
        String securityGroupId = AzureIDBuilder.buildSecurityGroupId(SECURITY_GROUP_NAME_DEFAULT);
        Completable deleteSecurityGroupObservable =
                azure.networkSecurityGroups().deleteByIdAsync(securityGroupId);

        String networkId = AzureIDBuilder.buildNetworkId(NETWORK_NAME_DEFAULT);
        Completable deleteNetworkObservable =
                azure.networks().deleteByIdAsync(networkId);

        Completable.merge(deleteNetworkObservable, deleteSecurityGroupObservable).subscribe(
                () -> {
                    System.out.println("Delete network style Fogbow complete");
                },
                (err) -> {
                    err.printStackTrace();
                }
        );
    }

    public static void createNetworkAsync(Azure azure) {
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();

        Observable<Indexable> securityGroupObservable = createSecurityGroup(azure);
        securityGroupObservable.flatMap(new Func1<Indexable, Observable<Indexable>>() {
            @Override
            public Observable<Indexable> call(Indexable indexable) {
                System.out.println("Security Group created");
                if (indexable instanceof NetworkSecurityGroup) {
                    NetworkSecurityGroup networkSecurityGroup = (NetworkSecurityGroup) indexable;
                    System.out.println("Creating virtual network");
                    return Observable.merge(
                            Observable.just(indexable),
                            createNetwork(azure, networkSecurityGroup));
                }
                return Observable.just(indexable);
            }
        }).subscribe();
    }

    private static Observable<Indexable> createNetwork(Azure azure, NetworkSecurityGroup networkSecurityGroup) {
        return azure.networks().define(NETWORK_NAME_DEFAULT)
                .withRegion(Constants.REGION_DEFAULT)
                .withExistingResourceGroup(networkSecurityGroup.resourceGroupName())
                .withAddressSpace("192.168.0.0/16")
                .defineSubnet(SUBNET_NAME_DEFAULT)
                .withAddressPrefix("192.168.0.0/16")
                .withExistingNetworkSecurityGroup(networkSecurityGroup)
                .attach()
                .createAsync();
    }

    private static Observable<Indexable> createSecurityGroup(Azure azure) {
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();

        return azure.networkSecurityGroups().define(SECURITY_GROUP_NAME_DEFAULT)
                .withRegion(Constants.REGION_DEFAULT)
                .withExistingResourceGroup(resourceGroupName)
                .defineRule("DenyInternetInComing")
                .denyInbound()
                .fromAddress("INTERNET")
                .fromAnyPort()
                .toAnyAddress()
                .toAnyPort()
                .withAnyProtocol()
                .attach()
                .createAsync();
    }

    private static PagedList<Network> getNetworks(Azure azure) {
        return azure.networks().list();
    }

    private static PagedList<NetworkInterface> getNetworkInterfaces(Azure azure) {
        return azure.networkInterfaces().list();
    }

}
