package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.ComputePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.util.DefaultLaunchCommandGenerator;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.AzureStateMapper;
import com.fgan.azure.fogbowmock.compute.model.AzureVirtualMachineImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureVirtualMachineRef;
import com.fgan.azure.fogbowmock.image.AzureImageOperation;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import com.fgan.azure.fogbowmock.util.AzureResourceInstanceId;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;

public class AzureComputePlugin implements ComputePlugin<AzureCloudUser> {

    private static final Logger LOGGER = Logger.getLogger(AzureComputePlugin.class);

    private static final String DEFAULT_NETWORK_INTERFACE_NAME_KEY = "default_network_interface_name";
    private static final String DEFAULT_RESOURCE_GROUP_NAME_KEY = "resource_group_name";
    private static final String DEFAULT_REGION_NAME_KEY = "region_name";

    private final AzureVirtualMachineOperation<AzureVirtualMachineOperationImpl> azureVirtualMachineRequest;
    private final DefaultLaunchCommandGenerator launchCommandGenerator;
    private final String defaultNetworkInterfaceName;
    private final Properties properties;
    private final String resourceGroupName;
    private final String regionName;

    public AzureComputePlugin(String confFilePath) {
        this.properties = PropertiesUtil.readProperties(confFilePath);

        this.defaultNetworkInterfaceName = this.properties.getProperty(DEFAULT_NETWORK_INTERFACE_NAME_KEY);
        this.resourceGroupName = this.properties.getProperty(DEFAULT_RESOURCE_GROUP_NAME_KEY);
        this.regionName = this.properties.getProperty(DEFAULT_REGION_NAME_KEY);
        this.launchCommandGenerator = new DefaultLaunchCommandGenerator();
        this.azureVirtualMachineRequest = new AzureVirtualMachineOperationImpl();
    }

    @Override
    public boolean isReady(String instanceState) {
        return AzureStateMapper.map(ResourceType.COMPUTE, instanceState).equals(InstanceState.READY);
    }

    @Override
    public boolean hasFailed(String instanceState) {
        return AzureStateMapper.map(ResourceType.COMPUTE, instanceState).equals(InstanceState.FAILED);
    }

    @Override
    public String requestInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Info.REQUESTING_INSTANCE_FROM_PROVIDER));
        String networkInterfaceId = getNetworkInterfaceId(computeOrder, azureCloudUser);
        String flavorName = this.azureVirtualMachineRequest.findFlavour(computeOrder, azureCloudUser);
        int diskSize = computeOrder.getDisk();
        AzureVirtualMachineImageRef azureVirtualMachineImage = AzureImageOperation.buildAzureVirtualMachineImageBy(computeOrder.getImageId());
        String virtualMachineName = AzureResourceInstanceId.generateAzureResourceNameBy(computeOrder);
        String userData = getUserData();
        String osUserName = computeOrder.getId();
        String osUserPassword = computeOrder.getId();
        String osComputeName = computeOrder.getId();

        AzureVirtualMachineRef azureVirtualMachineParameters = AzureVirtualMachineRef.builder()
                .virtualMachineName(virtualMachineName)
                .azureVirtualMachineImage(azureVirtualMachineImage)
                .networkInterfaceId(networkInterfaceId)
                .diskSize(diskSize)
                .size(flavorName)
                .osComputeName(osComputeName)
                .osUserName(osUserName)
                .osUserPassword(osUserPassword)
                .regionName(this.regionName)
                .resourceGroupName(this.resourceGroupName)
                .userData(userData)
                .build();

        this.azureVirtualMachineRequest.doCreateAsynchronously(azureVirtualMachineParameters, azureCloudUser);

        return AzureResourceInstanceId.generateFogbowInstanceIdBy(computeOrder);
    }

    private String getUserData() {
//        return this.launchCommandGenerator.createLaunchCommand(computeOrder);
        // TODO(chico) - Remove when It goes to the Fogbow context
        return com.fgan.azure.util.PropertiesUtil.getUserData();
    }

    // TODO(chico) - Finish; Study multi network interfaces behaviour.
    @VisibleForTesting
    String getNetworkInterfaceId(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        List<String> networkIds = computeOrder.getNetworkIds();
        if (networkIds.size() > 1) {
            throw new FogbowException("Multiple networks not allowed yed");
        } else if (networkIds.size() == 1) {
            return networkIds.stream().findFirst().get();
        }

        return AzureIdBuilder
                .configure(azureCloudUser)
                .buildNetworkInterfaceId(this.defaultNetworkInterfaceName);
    }

    @Override
    public ComputeInstance getInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        LOGGER.info(String.format(Messages.Info.GETTING_INSTANCE_S, computeOrder.getInstanceId()));
        return this.azureVirtualMachineRequest.getComputeInstance(computeOrder, azureCloudUser);
    }

    @Override
    public void deleteInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        LOGGER.info(String.format(Messages.Info.DELETING_INSTANCE_S, computeOrder.getInstanceId()));
        this.azureVirtualMachineRequest.doDeleteAsynchronously(computeOrder, azureCloudUser);
    }

}
