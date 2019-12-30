package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.ComputePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.util.DefaultLaunchCommandGenerator;
import cloud.fogbow.ras.core.plugins.interoperability.util.LaunchCommandGenerator;
import com.fgan.azure.fogbowmock.*;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;
import java.util.Properties;

public class AzureComputePlugin implements ComputePlugin<AzureCloudUser> {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AzureComputePlugin.class);

    // TODO(chico)
    private static final String DEFAULT_NETWORK_ID_KEY = "default_network_id";
    private static final String DEFAULT_RESOURCE_GROUP_NAME_KEY = "resource_group_name";
    private static final String DEFAULT_REGION_NAME_KEY = "region_name";

    private final LaunchCommandGenerator launchCommandGenerator;
    private final String defaultNetworkId;
    private final Properties properties;
    private final String resourceGroupName;
    private final String regionName;

    public AzureComputePlugin(String confFilePath) {
        this.properties = PropertiesUtil.readProperties(confFilePath);

        this.defaultNetworkId = this.properties.getProperty(DEFAULT_NETWORK_ID_KEY);
        this.resourceGroupName = this.properties.getProperty(DEFAULT_RESOURCE_GROUP_NAME_KEY);
        this.regionName = this.properties.getProperty(DEFAULT_REGION_NAME_KEY);
        this.launchCommandGenerator = new DefaultLaunchCommandGenerator();
    }

    @Override
    public String requestInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        LOGGER.info(String.format(Messages.Info.REQUESTING_INSTANCE_FROM_PROVIDER));
        String networkInterfaceId = getNetworkInterfaceId(computeOrder);
        String flavorSize = findFlavor(computeOrder, azureCloudUser);
        AzureVirtualMachineImage azureVirtualMachineImage = getImage(computeOrder.getImageId());
        String virtualMachineName = AzureResourceNameUtil.createVirtualMachineName(computeOrder.getId());
        String userData = this.launchCommandGenerator.createLaunchCommand(computeOrder);

        Azure azure = AzureClient.getAzure(azureCloudUser);
        Observable<Indexable> request = AzureVirtualMachineRequest.createRequest(azure, networkInterfaceId, this.resourceGroupName,
                virtualMachineName, this.regionName, azureVirtualMachineImage, userData, flavorSize, computeOrder.getId());

        return doRequestInstance(computeOrder, request);
    }

    @VisibleForTesting
    AzureVirtualMachineImage getImage(String imageId) {
        return null;
    }

    // TODO(chico) - Finish; Study multi network interfaces behaviour.
    @VisibleForTesting
    String getNetworkInterfaceId(ComputeOrder computeOrder) throws FogbowException {
        List<String> networkIds = computeOrder.getNetworkIds();
        if (!networkIds.isEmpty()) {
            throw new FogbowException("Multiple networks not allowed yed");
        }
        return this.defaultNetworkId;
    }

    @VisibleForTesting
    String doRequestInstance(ComputeOrder order, Observable<Indexable> request) {
        AzureVirtualMachineRequest.execureAsyncronously(request);
        String instanceId = AzureIDBuilderFogbow.buildAzureVirtualMachineId(order.getId());
        return instanceId;
    }

    // TODO(chico) - Finish implementation
    @VisibleForTesting
    String findFlavor(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) {
        return null;
    }

    @Override
    public ComputeInstance getInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        return null;
    }

    @Override
    public boolean isReady(String s) {
        return false;
    }

    @Override
    public boolean hasFailed(String s) {
        return false;
    }

    @Override
    public void deleteInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {

    }
}
