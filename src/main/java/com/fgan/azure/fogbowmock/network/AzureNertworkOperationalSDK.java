package com.fgan.azure.fogbowmock.network;

import com.fgan.azure.Constants;
import com.fgan.azure.api.network.exceptions.CreateNetworkException;
import com.fgan.azure.api.network.exceptions.CreateNetworkInterfaceException;
import com.fgan.azure.api.network.exceptions.CreateNetworkOperationException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.network.model.AzureCreateNetworkRef;
import com.fgan.azure.fogbowmock.util.AzureSchedulerManager;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.slf4j.LoggerFactory;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;

/**
 * AzureVirtualMachineOperationSDK uses the Reactive Programming and uses the RXJava library.
 */
public class AzureNertworkOperationalSDK implements AzureNetworkOperational {

    public static final String START_CREATE_NETWORK_ROLLBACK = "Start create network rollback";
    public static final String FINISH_CREATE_NETWORK_ROLLBACK = "End create network rollback";
    public static final String NETWORK_CREATION_STEP_1 = "Start network creation";
    public static final String NETWORK_CREATION_STEP_2 = "Network Creation proccess - Security Group Created";
    public static final String NETWORK_CREATION_STEP_3 = "Network Creation proccess - Network Created";
    public static final String NETWORK_CREATION_STEP_4 = "Network Creation proccess - Network Interface Created";
    public static final String NETWORK_CREATION_ERROR = "Error during the network creation";
    public static final String NETWORK_CREATION_STEP_5 = "Finish network creation";
//    public static final String NETWORK_DELETION_STEP_1 = "Start network deletion 1";
//    public static final String NETWORK_DELETION_STEP_2 = "Start network deletion 2";
//    public static final String NETWORK_DELETION_STEP_3 = "Start network deletion 3";
//    public static final String NETWORK_DELETION_STEP_4 = "Start network deletion 4";
//    public static final String NETWORK_DELETION_ERROR = "Error during the network deletion";
//    public static final String NETWORK_DELETION_STEP_5 = "Start network deletion 5";

    //    private static final Logger LOGGER = Logger.getLogger(AzureNertworkOperationalSDK.class);
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AzureNertworkOperationalSDK.class);
    private final Scheduler scheduler;

    public AzureNertworkOperationalSDK() {
        ExecutorService networkExecutor = AzureSchedulerManager.getNetworkExecutor();
        this.scheduler = Schedulers.from(networkExecutor);
    }

    @Override
    public void doCreateInstance(AzureCreateNetworkRef azureCreateNetworkRef, AzureCloudUser azureCloudUser) {

    }

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

    private Observable<Indexable> createSecurityGroupDefaultValues() {
        String securityGroupName = "";
        Region region = null;
        String defineRule = "DenyInternetInComing";
        String fromAddress = "INTERNET";
        return AzureNetworkSDK.createSecurityGroup(null, "", securityGroupName, region, defineRule, fromAddress);
    }

    @VisibleForTesting
    void doNetworkCreationStepThee(Indexable networkIndexable) {
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

    Observable<Indexable> createNetworkInterfaceDefaultValues(Network network) {
        String networkInterfaceName = "";
        Region region = Constants.REGION_DEFAULT;
        String resourceGroupName = network.resourceGroupName();
        String subnetName = "";
        return AzureNetworkSDK.createNetworkInterfaceAsync(null, networkInterfaceName, region, resourceGroupName,
                subnetName, network);
    }

    @VisibleForTesting
    Indexable doNetworkCreationStepTwo(Indexable indexableSecurityGroup) {
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

    Observable<Indexable> createNetworkDefaultValues(NetworkSecurityGroup networkSecurityGroup) {
        String networkName = "NETWORK_NAME_DEFAULT";
        Region region = Constants.REGION_DEFAULT;
        String networkResourceGroupName = networkSecurityGroup.resourceGroupName();
        String addressSpace = "192.168.0.0/16";
        String addressPrefix = addressSpace;
        String subnetName = "SUBNET_NAME_DEFAULT";
        return AzureNetworkSDK.createNetworkAsync(null, networkName, region, networkResourceGroupName,
                addressSpace, addressPrefix, subnetName, networkSecurityGroup);
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
    Completable getDeleteNetworkCompletable() {
//        String networkId = AzureIDBuilderGeneral.buildNetworkId("");
//        Completable deleteNetworkCompletable = this.networkApi.deleteNetworkByIdAsync(networkId);
//
//        LOGGER.debug(String.format("It will delete network named %s", networkId));
//        return deleteNetworkCompletable
//                .doOnError((error) -> {
//                    System.out.println("Error while trying to delete network");
//                });
        return null;
    }

    @VisibleForTesting
    Completable getDeleteSecurityGroupCompletable() {
//        String securityGroupId = AzureIDBuilderGeneral.buildSecurityGroupId(SECURITY_GROUP_NAME_DEFAULT);
//        Completable deleteSecurityGroupCompletable = this.networkApi.deleteSecurityGroupByIdAsync(securityGroupId);
//
//        LOGGER.debug(String.format("It will delete security group named %s", securityGroupId));
//        return deleteSecurityGroupCompletable
//                .doOnError((error) -> {
//                    System.out.println("Error while trying to delete security group ");
//                });
        return null;
    }

    private Completable getDeleteNetworkInterfaceCompletable() {
        return null;
    }

    @Override
    public void doGetInstance(String instanceId, AzureCloudUser azureCloudUser) {

    }

    @Override
    public void doDeleteInstance(String instanceId, AzureCloudUser azureCloudUser) {
        Completable deleteNetworkInterfaceCompletable = getDeleteNetworkInterfaceCompletable();
        Completable deleteNetworkCompletable = getDeleteNetworkCompletable();
        Completable deleteSecurityGroupCompletable = getDeleteSecurityGroupCompletable();

        Completable.concat(deleteNetworkInterfaceCompletable, deleteNetworkCompletable, deleteSecurityGroupCompletable)
                .subscribeOn(this.scheduler)
                .subscribe();
    }
}
