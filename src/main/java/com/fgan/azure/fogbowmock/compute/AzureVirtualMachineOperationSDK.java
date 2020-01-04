package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.network.NetworkApi;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import com.fgan.azure.fogbowmock.util.AzureSchedulerManager;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.apache.log4j.Logger;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * AzureVirtualMachineOperationSDK uses the Reactive Programming and uses the RXJava library.
 */
public class AzureVirtualMachineOperationSDK implements AzureVirtualMachineOperation {

    private static final Logger LOGGER = Logger.getLogger(AzureComputePlugin.class);

    private final ExecutorService virtualMachineExecutor;

    public AzureVirtualMachineOperationSDK() {
        this.virtualMachineExecutor = AzureSchedulerManager.getVirtualMachineExecutor();
    }

    /**
     * Create asynchronously because this operation takes a long time.
     */
    @Override
    public void doCreateInstance(AzureCreateVirtualMachineRef azureCreateVirtualMachineRef,
                                 AzureCloudUser azureCloudUser) throws FogbowException {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        String networkInterfaceId = azureCreateVirtualMachineRef.getNetworkInterfaceId();
        NetworkInterface networkInterface = getNetworkInterface(networkInterfaceId, azureCloudUser, azure);

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

        Observable<Indexable> virtualMachineAsync = ComputeApi.createVirtualMachineAsync(
                azure, virtualMachineName, region, resourceGroupName, networkInterface,
                imagePublished, imageOffer, imageSku, osUserName, osUserPassword, osComputeName,
                userData, diskSize, size);

        doCreateInstanceAsynchronously(virtualMachineAsync);
    }

    /**
     * Create
     */
    private void doCreateInstanceAsynchronously(Observable<Indexable> virtualMachineAsync) {
        virtualMachineAsync
                .subscribeOn(Schedulers.from(virtualMachineExecutor))
                .doOnSubscribe(() -> {
                    LOGGER.debug("Start asynchronous create virtual machine");
                })
                .doOnError((error -> {
                    LOGGER.debug("Error while creating virtual machine asynchounously");
                }))
                .doOnCompleted(() -> {
                    LOGGER.debug("End asynchronous create virtual machine");
                })
                .subscribe();
    }

    private NetworkInterface getNetworkInterface(String fogbowNetworkInterfaceId,
                                                 AzureCloudUser azureCloudUser,
                                                 Azure azure)
            throws FogbowException {

        try {
            String azureNetworkInterfaceId = AzureIdBuilder
                    .configure(azureCloudUser)
                    .buildNetworkInterfaceId(fogbowNetworkInterfaceId);
            return NetworkApi.getNetworkInterface(azure, azureNetworkInterfaceId);
        } catch (Exception e) {
            throw new FogbowException("", e);
        }
    }

    @Override
    public String findVirtualMachineSizeName(int memoryRequired, int vCpuRequired, AzureCloudUser azureCloudUser)
            throws FogbowException {

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
            throw new NoAvailableResourcesException();
        }

        return firstVirtualMachineSize.name();
    }

    @Override
    public AzureGetVirtualMachineRef doGetInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = ComputeApi.getVirtualMachineById(azure, azureInstanceId);
        } catch (FogbowException e) {
            throw new InstanceNotFoundException();
        }

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

    public VirtualMachineSize findVirtualMachineSizeByName(String virtualMachineSizeNameWanted,
                                                           Azure azure) {

        PagedList<VirtualMachineSize> virtualMachineSizes = ComputeApi.getVirtualMachineSizes(azure);
        return virtualMachineSizes.stream()
                .filter((virtualMachineSize) -> virtualMachineSizeNameWanted.equals(virtualMachineSize.name()))
                .findFirst().get();
    }

    /**
     * Delete asynchronously because this operation takes a long time.
     */
    @Override
    public void doDeleteInstance(String azureInstanceId, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientCacheManager.getAzure(azureCloudUser);

        Completable completable = ComputeApi.deleteVirtualMachineAsync(azure, azureInstanceId);
        doDeleteInstanceAsynchronously(completable);
    }

    private void doDeleteInstanceAsynchronously(Completable completable) {
        completable
                .subscribeOn(Schedulers.from(virtualMachineExecutor))
                .doOnSubscribe((a) -> {
                    LOGGER.debug("Start asynchronous delete virtual machine");
                })
                .doOnError((error -> {
                    LOGGER.debug("Error while deleting virtual machine asynchounously");
                }))
                .doOnCompleted(() -> {
                    LOGGER.debug("End asynchronous delete virtual machine");
                })
                .subscribe();
    }

}
