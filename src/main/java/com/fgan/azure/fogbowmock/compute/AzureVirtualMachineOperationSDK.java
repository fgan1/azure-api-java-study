package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.network.NetworkApi;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.Messages;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.fgan.azure.fogbowmock.util.AzureSchedulerManager;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.slf4j.LoggerFactory;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * AzureVirtualMachineOperationSDK uses the Reactive Programming and uses the RXJava library.
 */
public class AzureVirtualMachineOperationSDK implements AzureVirtualMachineOperation {

//    private static final Logger LOGGER = Logger.getLogger(AzureVirtualMachineOperationSDK.class);
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AzureVirtualMachineOperationSDK.class);

    private final ExecutorService virtualMachineExecutor;

    public AzureVirtualMachineOperationSDK() {
        this.virtualMachineExecutor = AzureSchedulerManager.getVirtualMachineExecutor();
    }

    /**
     * Create asynchronously because this operation takes a long time.
     */
    @Override
    public void doCreateInstance(AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
                                 AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.ResourceNotFound {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        Observable<Indexable> virtualMachineAsync = getAzureVirtualMachineObservable(
                azureCreateVirtualMachineRef, azure);

        subscribeCreateVirtualMachine(virtualMachineAsync);
    }

    @VisibleForTesting
    Observable<Indexable> getAzureVirtualMachineObservable(
            AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
            Azure azure) throws AzureException.ResourceNotFound {

        String networkInterfaceId = azureCreateVirtualMachineRef.getNetworkInterfaceId();
        NetworkInterface networkInterface = getNetworkInterface(networkInterfaceId, azure);
        String resourceGroupName = azureCreateVirtualMachineRef.getResourceGroupName();
        String regionName = azureCreateVirtualMachineRef.getRegionName();
        String virtualMachineName = azureCreateVirtualMachineRef.getVirtualMachineName();
        String osUserName = azureCreateVirtualMachineRef.getOsUserName();
        String osUserPassword = azureCreateVirtualMachineRef.getOsUserPassword();
        String osComputeName = azureCreateVirtualMachineRef.getOsComputeName();
        String userData = azureCreateVirtualMachineRef.getUserData();
        String size = azureCreateVirtualMachineRef.getSize();
        int diskSize = azureCreateVirtualMachineRef.getDiskSize();
        AzureGetImageRef azureVirtualMachineImage = azureCreateVirtualMachineRef.getAzureVirtualMachineImage();
        Region region = Region.findByLabelOrName(regionName);
        String imagePublished = azureVirtualMachineImage.getPublisher();
        String imageOffer = azureVirtualMachineImage.getOffer();
        String imageSku = azureVirtualMachineImage.getSku();

        return AzureVirtualMachineSDK.buildVirtualMachineObservable(
                azure, virtualMachineName, region, resourceGroupName, networkInterface,
                imagePublished, imageOffer, imageSku, osUserName, osUserPassword, osComputeName,
                userData, diskSize, size);
    }

    /**
     * Execute create Virtual Machine observable and set its behaviour.
     */
    @VisibleForTesting
    void subscribeCreateVirtualMachine(Observable<Indexable> virtualMachineObservable) {
        Scheduler scheduler = getScheduler();

        virtualMachineObservable
                .subscribeOn(scheduler)
                .doOnSubscribe(() -> {
                    LOGGER.info(Messages.START_CREATE_VM_ASYNC_BEHAVIOUR);
                })
                .onErrorReturn((error -> {
                    LOGGER.error(Messages.ERROR_CREATE_VM_ASYNC_BEHAVIOUR, error);
                    return null;
                }))
                .doOnCompleted(() -> {
                    LOGGER.info(Messages.END_CREATE_VM_ASYNC_BEHAVIOUR);
                })
                .subscribe();
    }

    @VisibleForTesting
    Scheduler getScheduler() {
        return Schedulers.from(this.virtualMachineExecutor);
    }

    private NetworkInterface getNetworkInterface(String azureNetworkInterfaceId,
                                                 Azure azure)
            throws AzureException.ResourceNotFound {

        try {
            return NetworkApi.getNetworkInterface(azure, azureNetworkInterfaceId);
        } catch (Exception e) {
            throw new AzureException.ResourceNotFound(e);
        }
    }

    @Override
    public String findVirtualMachineSizeName(int memoryRequired, int vCpuRequired, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.NoAvailableResourcesException {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        PagedList<VirtualMachineSize> virtualMachineSizes = ComputeApi.getVirtualMachineSizes(azure);
        VirtualMachineSize firstVirtualMachineSize = virtualMachineSizes.stream()
                .filter((virtualMachineSize) ->
                        virtualMachineSize.memoryInMB() >= memoryRequired &&
                                virtualMachineSize.numberOfCores() >= vCpuRequired
                )
                .sorted(Comparator
                        .comparingInt(VirtualMachineSize::memoryInMB)
                        .thenComparingInt(VirtualMachineSize::numberOfCores))
                .findFirst().get();

        if (firstVirtualMachineSize == null) {
            throw new AzureException.NoAvailableResourcesException("");
        }

        return firstVirtualMachineSize.name();
    }

    @Override
    public AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.ResourceNotFound {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, azureInstanceId);
        String virtualMachineSizeName = virtualMachine.size().toString();
        VirtualMachineSize virtualMachineSize = findVirtualMachineSizeByName(virtualMachineSizeName, azure);
        int vCPU = virtualMachineSize.numberOfCores();
        int memory = virtualMachineSize.memoryInMB();
        int disk = virtualMachine.osDiskSize();
        String id = virtualMachine.vmId();
        String cloudState = virtualMachine.provisioningState();
        String name = virtualMachine.name();
        String primaryPrivateIp = virtualMachine.getPrimaryNetworkInterface().primaryPrivateIP();
        List<String> ipAddresses = Arrays.asList(primaryPrivateIp);

        return AzureGetVirtualMachineRef.builder()
                .disk(disk)
                .id(id)
                .cloudState(cloudState)
                .ipAddresses(ipAddresses)
                .memory(memory)
                .name(name)
                .vCPU(vCPU)
                .build();
    }

    @VisibleForTesting
    VirtualMachineSize findVirtualMachineSizeByName(String virtualMachineSizeNameWanted,
                                                           Azure azure) {

        PagedList<VirtualMachineSize> virtualMachineSizes = ComputeApi.getVirtualMachineSizes(azure);
        return virtualMachineSizes.stream()
                .filter((virtualMachineSize) -> virtualMachineSizeNameWanted.equals(virtualMachineSize.name()))
                .findFirst()
                .get();
    }

    /**
     * Delete asynchronously because this operation takes a long time.
     */
    @Override
    public void doDeleteInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        Completable completable = ComputeApi.deleteVirtualMachineAsync(azure, azureInstanceId);

        subscribeDeleteVirtualMachine(completable);
    }

    /**
     * Execute delete Virtual Machine observable and set its behaviour.
     */
    @VisibleForTesting
    void subscribeDeleteVirtualMachine(Completable deleteVirtualMachineCompletable) {
        Scheduler scheduler = getScheduler();

        deleteVirtualMachineCompletable
                .subscribeOn(scheduler)
                .doOnSubscribe((a) -> {
                    LOGGER.info(Messages.START_DELETE_VM_ASYNC_BEHAVIOUR);
                })
                .onErrorComplete((error -> {
                    LOGGER.error(Messages.ERROR_DELETE_VM_ASYNC_BEHAVIOUR);
                    return null;
                }))
                .doOnCompleted(() -> {
                    LOGGER.info(Messages.END_DELETE_VM_ASYNC_BEHAVIOUR);
                })
                .subscribe();
    }

}
