package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.UserData;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.ManagerApi;
import com.fgan.azure.api.network.NetworkApi;
import com.fgan.azure.fogbowmock.AzureClientUtil;
import com.fgan.azure.fogbowmock.AzureCloudUser;
import com.fgan.azure.fogbowmock.AzureIDBuilderFogbow;
import com.fgan.azure.fogbowmock.AzureVirtualMachineImage;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.slf4j.LoggerFactory;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AzureVirtualMachineOperationImpl implements AzureVirtualMachineOperation {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AzureComputePlugin.class);

    @Override
    public void doCreateAsynchronously(AzureVirtualMachineParameters azureVirtualMachineParameters,
                                       AzureCloudUser azureCloudUser) throws FogbowException {

        Azure azure = AzureClientUtil.getAzure(azureCloudUser);

        String networkInterfaceId = azureVirtualMachineParameters.getNetworkInterfaceId();
        String resourceGroupName = azureVirtualMachineParameters.getResourceGroupName();
        String regionName = azureVirtualMachineParameters.getRegionName();
        String virtualMachineName = azureVirtualMachineParameters.getVirtualMachineName();
        String osUserName = azureVirtualMachineParameters.getOsUserName();
        String osUserPassword = azureVirtualMachineParameters.getOsUserPassword();
        String osComputeName = azureVirtualMachineParameters.getOsComputeName();
        String userData = azureVirtualMachineParameters.getUserData();
        String size = azureVirtualMachineParameters.getSize();
        AzureVirtualMachineImage azureVirtualMachineImage = azureVirtualMachineParameters.getAzureVirtualMachineImage();
        NetworkInterface networkInterface = new NetworkApi(azure).getNetworkInterface(azure, networkInterfaceId);
        ResourceGroup resourceGroup = ManagerApi.getResourceGroup(azure, resourceGroupName);
        Region region = Region.findByLabelOrName(regionName);
        String imagePublished = azureVirtualMachineImage.getPublisher();
        String imageOffer = azureVirtualMachineImage.getOffer();
        String imageSku = azureVirtualMachineImage.getSku();

        Observable<Indexable> virtualMachineAsync = ComputeApi.createVirtualMachineAsync(azure, virtualMachineName, region,
                resourceGroup, networkInterface,
                imagePublished, imageOffer, imageSku,
                osUserName, osUserPassword, osComputeName,
                userData, size);

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

    @Override
    public String findFlavour(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientUtil.getAzure(azureCloudUser);

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

        Azure azure = AzureClientUtil.getAzure(azureCloudUser);
        String azureInstanceId = AzureIDBuilderFogbow.buildAzureVirtualMachineId(computeOrder.getInstanceId());
        String virtualMachineId = AzureIDBuilderFogbow.buildAzureVirtualMachineId(azureInstanceId);
        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);

        String id = virtualMachine.vmId();
        String cloudName = virtualMachine.computerName();
        String name = virtualMachine.computerName();
        int vCPU = 0;
        int memory = 0;
        int disk = virtualMachine.osDiskSize();
        List<String> ipAddresses = Arrays.asList(virtualMachine.getPrimaryNetworkInterface().primaryPrivateIP());
        String imageId = computeOrder.getImageId();
        String publicKey = computeOrder.getPublicKey();
        List<UserData> userData = computeOrder.getUserData();
        return new ComputeInstance(id, cloudName, name, vCPU, memory, disk, ipAddresses, imageId, publicKey, userData);
    }

    @Override
    public void doDeleteAsynchronously(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        Azure azure = AzureClientUtil.getAzure(azureCloudUser);
        String azureInstanceId = AzureIDBuilderFogbow.buildAzureVirtualMachineId(computeOrder.getInstanceId());
        Completable completable = ComputeApi.deleteVirtualMachineAsync(azure, azureInstanceId);
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
