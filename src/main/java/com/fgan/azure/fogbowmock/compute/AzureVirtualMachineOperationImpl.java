package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.UserData;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.network.NetworkApi;
import com.fgan.azure.fogbowmock.compute.model.AzureVirtualMachineImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureVirtualMachineRef;
import com.fgan.azure.fogbowmock.util.AzureClientCache;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
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

public class AzureVirtualMachineOperationImpl implements AzureVirtualMachineOperation {

    private static final Logger LOGGER = Logger.getLogger(AzureComputePlugin.class);

    @Override
    public void doCreateAsynchronously(AzureVirtualMachineRef azureVirtualMachineParameters,
                                       AzureCloudUser azureCloudUser) throws FogbowException {

        Azure azure = AzureClientCache.getAzure(azureCloudUser);

        String fogbowNetworkInterfaceId = azureVirtualMachineParameters.getNetworkInterfaceId();
        NetworkInterface networkInterface = getNetworkInterface(fogbowNetworkInterfaceId, azureCloudUser, azure);

        String resourceGroupName = azureVirtualMachineParameters.getResourceGroupName();
        String regionName = azureVirtualMachineParameters.getRegionName();
        String virtualMachineName = azureVirtualMachineParameters.getVirtualMachineName();
        String osUserName = azureVirtualMachineParameters.getOsUserName();
        String osUserPassword = azureVirtualMachineParameters.getOsUserPassword();
        String osComputeName = azureVirtualMachineParameters.getOsComputeName();
        String userData = azureVirtualMachineParameters.getUserData();
        String size = azureVirtualMachineParameters.getSize();
        int diskSize = azureVirtualMachineParameters.getDiskSize();
        AzureVirtualMachineImageRef azureVirtualMachineImage = azureVirtualMachineParameters.getAzureVirtualMachineImage();
        Region region = Region.findByLabelOrName(regionName);
        String imagePublished = azureVirtualMachineImage.getPublisher();
        String imageOffer = azureVirtualMachineImage.getOffer();
        String imageSku = azureVirtualMachineImage.getSku();

        Observable<Indexable> virtualMachineAsync = ComputeApi.createVirtualMachineAsync(
                azure, virtualMachineName, region, resourceGroupName, networkInterface,
                imagePublished, imageOffer, imageSku, osUserName, osUserPassword, osComputeName,
                userData, diskSize, size);

        virtualMachineAsync
                .subscribeOn(Schedulers.computation())
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
    public String findFlavour(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientCache.getAzure(azureCloudUser);

        PagedList<VirtualMachineSize> virtualMachineSizes = ComputeApi.getVirtualMachineSizes(azure);
        VirtualMachineSize firstVirtualMachineSize = virtualMachineSizes.stream()
                .filter((virtualMachineSize) ->
                        virtualMachineSize.memoryInMB() >= computeOrder.getMemory() &&
                                virtualMachineSize.numberOfCores() >= computeOrder.getvCPU()
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
    public ComputeInstance getComputeInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientCache.getAzure(azureCloudUser);

        VirtualMachine virtualMachine = null;
        try {
            String instanceId = computeOrder.getInstanceId();
            virtualMachine = ComputeApi.getVirtualMachineById(azure, instanceId);
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
        String name = virtualMachine.computerName();
        String primaryPrivateIp = virtualMachine.getPrimaryNetworkInterface().primaryPrivateIP();
        List<String> ipAddresses = Arrays.asList(primaryPrivateIp);
        String imageId = computeOrder.getImageId();
        String publicKey = computeOrder.getPublicKey();
        List<UserData> userData = computeOrder.getUserData();

        return new ComputeInstance(id, cloudState, name, vCPU, memory, disk, ipAddresses, imageId, publicKey, userData);
    }

    public VirtualMachineSize findVirtualMachineSizeByName(String virtualMachineSizeNameWanted,
                                                           Azure azure) {

        PagedList<VirtualMachineSize> virtualMachineSizes = ComputeApi.getVirtualMachineSizes(azure);
        return virtualMachineSizes.stream()
                .filter((virtualMachineSize) -> virtualMachineSizeNameWanted.equals(virtualMachineSize.name()))
                .findFirst().get();
    }

    @Override
    public void doDeleteAsynchronously(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientCache.getAzure(azureCloudUser);

        String instanceId = computeOrder.getInstanceId();

        Completable completable = ComputeApi.deleteVirtualMachineAsync(azure, instanceId);
        completable
                .subscribeOn(Schedulers.computation())
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
