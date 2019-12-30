package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.api.ComputeApi;
import com.fgan.azure.api.ManagerApi;
import com.fgan.azure.api.network.NetworkApi;
import com.fgan.azure.fogbowmock.AzureVirtualMachineImage;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AzureVirtualMachineRequest {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AzureComputePlugin.class);

    public static Observable<Indexable> createRequest(Azure azure, String networkInterfaceId,
                                                      String resourceGroupName, String virtualMachineName,
                                                      String regionName, AzureVirtualMachineImage azureVirtualMachineImage,
                                                      String userData, String size, String orderId) {

        NetworkInterface networkInterface = new NetworkApi(azure).getNetworkInterface(azure, networkInterfaceId);
        ResourceGroup resourceGroup = ManagerApi.getResourceGroup(azure, resourceGroupName);

        Region region = Region.findByLabelOrName(regionName);
        String imagePublished = azureVirtualMachineImage.getPublisher();
        String imageOffer = azureVirtualMachineImage.getOffer();
        String imageSku = azureVirtualMachineImage.getSku();
        String osUserName = orderId;
        // TODO(chico): Generate password
        String osUserPassword = orderId;
        String osComputeName = orderId;

        return ComputeApi.createVirtualMachineAsync(azure, virtualMachineName, region,
                resourceGroup, networkInterface,
                imagePublished, imageOffer, imageSku,
                osUserName, osUserPassword, osComputeName,
                userData, size);
    }

    public static void execureAsyncronously(Observable<Indexable> observable) {
        observable
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

}
