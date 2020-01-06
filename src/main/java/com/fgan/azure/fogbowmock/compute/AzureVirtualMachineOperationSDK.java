package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.api.ComputeApi;
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
        NetworkInterface networkInterface = AzureNetworkSDK.getNetworkInterface(azure, networkInterfaceId);
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

    @Override
    public String findVirtualMachineSize(int memoryRequired, int vCpuRequired,
                                         String regionName, AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.NoAvailableResources,
            AzureException.ResourceNotFound {

        LOGGER.debug(String.format("Trying to find the VM size that fits with memory(%s) and vCpu(%s) at region %s"
                , memoryRequired, vCpuRequired, regionName));
        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        Region region = Region.findByLabelOrName(regionName);
        PagedList<VirtualMachineSize> virtualMachineSizes =
                AzureVirtualMachineSDK.getVirtualMachineSizes(azure, region);
        VirtualMachineSize firstVirtualMachineSize = virtualMachineSizes.stream()
                .filter((virtualMachineSize) ->
                                virtualMachineSize.memoryInMB() >= memoryRequired &&
                                virtualMachineSize.numberOfCores() >= vCpuRequired
                )
                .sorted(Comparator
                        .comparingInt(VirtualMachineSize::memoryInMB)
                        .thenComparingInt(VirtualMachineSize::numberOfCores))
                .findFirst()
                .orElseThrow(() -> new AzureException.NoAvailableResources(
                        "There is no virtual machine that fits with the requirements"));

        return firstVirtualMachineSize.name();
    }

    @Override
    public AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, String regionName,
                                                   AzureCloudUser azureCloudUser)
            throws AzureException.Unauthorized, AzureException.ResourceNotFound,
            AzureException.NoAvailableResources {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        VirtualMachine virtualMachine = AzureVirtualMachineSDK.getVirtualMachineById(azure, azureInstanceId);
        String virtualMachineSizeName = virtualMachine.size().toString();
        VirtualMachineSize virtualMachineSize = findVirtualMachineSizeByName(virtualMachineSizeName, regionName, azure);
        int vCPU = virtualMachineSize.numberOfCores();
        int memory = virtualMachineSize.memoryInMB();
        int disk = virtualMachine.osDiskSize();
        String id = virtualMachine.vmId();
        String cloudState = virtualMachine.provisioningState();
        String name = virtualMachine.name();
        String primaryPrivateIp = virtualMachine.getPrimaryNetworkInterface().primaryPrivateIP();
        List<String> ipAddresses = Arrays.asList(primaryPrivateIp);

        return AzureGetVirtualMachineRef.builder()
                .cloudState(cloudState)
                .ipAddresses(ipAddresses)
                .disk(disk)
                .memory(memory)
                .name(name)
                .vCPU(vCPU)
                .id(id)
                .build();
    }

    @VisibleForTesting
    VirtualMachineSize findVirtualMachineSizeByName(String virtualMachineSizeNameWanted, String regionName, Azure azure)
            throws AzureException.NoAvailableResources, AzureException.ResourceNotFound {

        Region region = Region.findByLabelOrName(regionName);
        PagedList<VirtualMachineSize> virtualMachineSizes = AzureVirtualMachineSDK.getVirtualMachineSizes(azure, region);
        return virtualMachineSizes.stream()
                .filter((virtualMachineSize) -> virtualMachineSizeNameWanted.equals(virtualMachineSize.name()))
                .findFirst()
                .orElseThrow(() -> new AzureException.NoAvailableResources(
                        "There is no virtual machine that fits with the requirements"));
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
