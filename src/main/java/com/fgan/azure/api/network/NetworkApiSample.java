package com.fgan.azure.api.network;

import com.fgan.azure.Constants;
import com.fgan.azure.api.network.exceptions.CreateNetworkException;
import com.fgan.azure.api.network.exceptions.CreateNetworkInterfaceException;
import com.fgan.azure.api.network.exceptions.CreateNetworkOperationException;
import com.fgan.azure.util.AzureIDBuilder;
import com.fgan.azure.util.GeneralPrintUtil;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Samples Network operation:
 * - https://github.com/Azure-Samples/network-java-manage-network-interface
 * - https://github.com/Azure-Samples/network-java-manage-virtual-network
 * - https://github.com/Azure-Samples/network-java-manage-virtual-network-async
 */
public class NetworkApiSample {

    private final static Logger LOGGER = LoggerFactory.getLogger(NetworkApiSample.class);
    public static final String START_CREATE_NETWORK_ROLLBACK = "Start create network rollback";
    public static final String FINISH_CREATE_NETWORK_ROLLBACK = "End create network rollback";

    public static final String SECURITY_GROUP_NAME_DEFAULT = Constants.PREFIX + "security-group";
    public static final String NETWORK_NAME_DEFAULT = Constants.PREFIX + "network";
    public static final String NETWORK_INTERFACE_NAME_DEFAULT = Constants.PREFIX + "network-interface";
    public static final String SUBNET_NAME_DEFAULT = Constants.PREFIX + "subnet";
    private static NetworkApiSample instance = new NetworkApiSample();
    private static NetworkApi networkApi;

    public static NetworkApiSample build(Azure azure) {
        networkApi = new NetworkApi(azure);
        return instance;
    }

    public void printInformation() {
        PagedList<Network> networks = networkApi.getNetworks();
        GeneralPrintUtil.printNetworksLines(networks);

        PagedList<NetworkInterface> networkInterfaces = networkApi.getNetworkInterfaces();
        GeneralPrintUtil.printNetworkInterfacessLines(networkInterfaces);
    }

    public void printSpecificNetworkInformationById(Azure azure, String networkId) {
        Network network = networkApi.getNetwork(networkId);
        GeneralPrintUtil.printLines(network::name, network::regionName, network::id);
    }

    public void printSpecificNetworkInformationByName(Azure azure, String networkName) {
        String networkId = AzureIDBuilder.buildNetworkId(networkName);
        printSpecificNetworkInformationById(azure, networkId);
    }

    public Observable<Indexable> createSecurityGroupDefaultValues() {
        String securityGroupName = SECURITY_GROUP_NAME_DEFAULT;
        Region region = Constants.REGION_DEFAULT;
        String defineRule = "DenyInternetInComing";
        String fromAddress = "INTERNET";
        return networkApi.createSecurityGroup(securityGroupName, region, defineRule, fromAddress);
    }

    /**
     *
     */
    public void createNetworkCreationFogbowStyle() {
        Observable<Indexable> networkCreationFogbowObservable = buildNetworkCreationFogbowObservable();
        networkCreationFogbowObservable
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    public static final String NETWORK_CREATION_STEP_1 = "Start network creation";
    public static final String NETWORK_CREATION_STEP_2 = "Network Creation proccess - Security Group Created";
    public static final String NETWORK_CREATION_STEP_3 = "Network Creation proccess - Network Created";
    public static final String NETWORK_CREATION_STEP_4 = "Network Creation proccess - Network Interface Created";
    public static final String NETWORK_CREATION_ERROR = "Error during the network creation";
    public static final String NETWORK_CREATION_STEP_5 = "Finish network creation";


    @VisibleForTesting
    Observable<Indexable> buildNetworkCreationFogbowObservable() {
        Observable<Indexable> securityGroupObservable = createSecurityGroupDefaultValues();
        return securityGroupObservable
                .doOnSubscribe(() -> {
                    LOGGER.info(NETWORK_CREATION_STEP_1);
                })
                .doOnNext((indexableSecurityGroup) -> {
                    LOGGER.info(NETWORK_CREATION_STEP_2);
                    Indexable networkIndexable = doNetworkCreationStepTwo(indexableSecurityGroup);
                    LOGGER.info(NETWORK_CREATION_STEP_3);
                    doNetworkCreationStepThee(networkIndexable);
                    LOGGER.info(NETWORK_CREATION_STEP_4);
                })
                .doOnError((error) -> {
                    LOGGER.error(NETWORK_CREATION_ERROR, error);
                    doNetworkCreationRollback(error);
                })
                .doOnCompleted(() -> {
                    LOGGER.info(NETWORK_CREATION_STEP_5);
                });
    }

    @VisibleForTesting
    void doNetworkCreationRollback(Throwable throwable) {
        if (throwable instanceof CreateNetworkOperationException == false) {
            LOGGER.error("Error unknown in the CreateNetwork rollback", throwable);
            return;
        }

        LOGGER.info(START_CREATE_NETWORK_ROLLBACK);
        Completable rollbackCompletable = getDeleteSecurityGroupCompletable();

        if (throwable instanceof CreateNetworkInterfaceException) {
            Completable deleteNetworkCompletable = getDeleteNetworkCompletable();
            rollbackCompletable.concatWith(deleteNetworkCompletable);
        }

        rollbackCompletable
                .doOnCompleted(() -> {
                    LOGGER.info(FINISH_CREATE_NETWORK_ROLLBACK);
                })
                .await();
    }

    @VisibleForTesting
    Completable getDeleteSecurityGroupCompletable() {
        String securityGroupId = AzureIDBuilder.buildSecurityGroupId(SECURITY_GROUP_NAME_DEFAULT);
        Completable deleteSecurityGroupCompletable = this.networkApi.deleteSecurityGroupByIdAsync(securityGroupId);

        LOGGER.debug(String.format("It will delete security group named %s", securityGroupId));
        return deleteSecurityGroupCompletable
                .doOnError((error) -> {
                    System.out.println("Error while trying to delete security group ");
                });
    }

    @VisibleForTesting
    Completable getDeleteNetworkCompletable() {
        String networkId = AzureIDBuilder.buildNetworkId(NETWORK_NAME_DEFAULT);
        Completable deleteNetworkCompletable = this.networkApi.deleteNetworkByIdAsync(networkId);

        LOGGER.debug(String.format("It will delete network named %s", networkId));
        return deleteNetworkCompletable
                .doOnError((error) -> {
                    System.out.println("Error while trying to delete network");
                });
    }

    @VisibleForTesting
    void doNetworkCreationStepThee(Indexable networkIndexable) {
        if (networkIndexable instanceof Network == false) {
            throw new CreateNetworkInterfaceException("It is not a Network Indexable");
        }

        try {
            Network network = (Network) networkIndexable;
            Observable<Indexable> networkInterfaceObservable = createNetworkInterfaceDefaultValues(network);
            networkInterfaceObservable
                    .toBlocking()
                    .subscribe();
        } catch (RuntimeException e) {
            throw new CreateNetworkInterfaceException("It is not a Network Indexable", e);
        }
    }

    @VisibleForTesting
    Indexable doNetworkCreationStepTwo(Indexable indexableSecurityGroup) {
        if (indexableSecurityGroup instanceof NetworkSecurityGroup == false) {
            throw new CreateNetworkException("It is not a NetworkSecurityGroup indexable");
        }

        try {
            NetworkSecurityGroup networkSecurityGroup = (NetworkSecurityGroup) indexableSecurityGroup;
            Observable<Indexable> networkObservable = createNetworkDefaultValues(networkSecurityGroup);
            return networkObservable
                    .toBlocking()
                    .first();
        } catch (RuntimeException e) {
            throw new CreateNetworkException("It is not a Network Indexable", e);
        }
    }

    Observable<Indexable> createNetworkInterfaceDefaultValues(Network network) {
        String networkInterfaceName = NETWORK_INTERFACE_NAME_DEFAULT;
        Region region = Constants.REGION_DEFAULT;
        String resourceGroupName = network.resourceGroupName();
        String subnetName = SUBNET_NAME_DEFAULT;
        return networkApi.createNetworkInterfaceAsync(networkInterfaceName, region, resourceGroupName,
                subnetName, network);
    }

    Observable<Indexable> createNetworkDefaultValues(NetworkSecurityGroup networkSecurityGroup) {
        String networkName = NETWORK_NAME_DEFAULT;
        Region region = Constants.REGION_DEFAULT;
        String networkResourceGroupName = networkSecurityGroup.resourceGroupName();
        String addressSpace = "192.168.0.0/16";
        String addressPrefix = addressSpace;
        String subnetName = SUBNET_NAME_DEFAULT;
        return networkApi.createNetworkAsync(networkName, region, networkResourceGroupName,
                addressSpace, addressPrefix, subnetName, networkSecurityGroup);
    }

    public void deleteNetworkFogbowStyle() {
        String securityGroupId = AzureIDBuilder.buildSecurityGroupId(SECURITY_GROUP_NAME_DEFAULT);
        Completable deleteSecurityGroupObservable = networkApi.deleteSecurityGroupByIdAsync(securityGroupId);

        String networkId = AzureIDBuilder.buildNetworkId(NETWORK_NAME_DEFAULT);
        Completable deleteNetworkObservable = networkApi.deleteNetworkByIdAsync(networkId);

        Completable.merge(deleteNetworkObservable, deleteSecurityGroupObservable).subscribe(
                () -> {
                    System.out.println("Delete network style Fogbow complete");
                },
                (err) -> {
                    err.printStackTrace();
                }
        );
    }

}
