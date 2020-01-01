package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.ComputePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2StateMapper;
import cloud.fogbow.ras.core.plugins.interoperability.util.DefaultLaunchCommandGenerator;
import cloud.fogbow.ras.core.plugins.interoperability.util.LaunchCommandGenerator;
import com.fgan.azure.fogbowmock.*;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.LoggerFactory;

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
    private final AzureVirtualMachineOperation<AzureVirtualMachineOperationImpl> azureVirtualMachineRequest;

    public AzureComputePlugin(String confFilePath) {
        this.properties = PropertiesUtil.readProperties(confFilePath);

        this.defaultNetworkId = this.properties.getProperty(DEFAULT_NETWORK_ID_KEY);
        this.resourceGroupName = this.properties.getProperty(DEFAULT_RESOURCE_GROUP_NAME_KEY);
        this.regionName = this.properties.getProperty(DEFAULT_REGION_NAME_KEY);
        this.launchCommandGenerator = new DefaultLaunchCommandGenerator();
        this.azureVirtualMachineRequest = new AzureVirtualMachineOperationImpl();
    }

    @Override
    public boolean isReady(String instanceState) {
        return AwsV2StateMapper.map(ResourceType.COMPUTE, instanceState).equals(InstanceState.READY);
    }

    @Override
    public boolean hasFailed(String s) {
        return false;
    }

    @Override
    public String requestInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Info.REQUESTING_INSTANCE_FROM_PROVIDER));
        String networkInterfaceId = getNetworkInterfaceId(computeOrder);
        String flavorName = this.azureVirtualMachineRequest.findFlavour(computeOrder, azureCloudUser);
        AzureVirtualMachineImage azureVirtualMachineImage = AzureImageRepository.buildAzureVirtualMachineImageBy(computeOrder.getImageId());
        String virtualMachineName = AzureResourceNameUtil.createVirtualMachineName(computeOrder);
        String userData = this.launchCommandGenerator.createLaunchCommand(computeOrder);
        String osUserName = computeOrder.getId();
        String osUserPassword = computeOrder.getId();
        String osComputeName = computeOrder.getId();

        AzureVirtualMachineParameters azureVirtualMachineParameters = GenericBuilder
                .of(AzureVirtualMachineParameters::new)
                .with(AzureVirtualMachineParameters::setNetworkInterfaceId, networkInterfaceId)
                .with(AzureVirtualMachineParameters::setResourceGroupName, this.resourceGroupName)
                .with(AzureVirtualMachineParameters::setRegionName, this.regionName)
                .with(AzureVirtualMachineParameters::setAzureVirtualMachineImage, azureVirtualMachineImage)
                .with(AzureVirtualMachineParameters::setNetworkInterfaceId, networkInterfaceId)
                .with(AzureVirtualMachineParameters::setOsComputeName, osComputeName)
                .with(AzureVirtualMachineParameters::setOsUserName, osUserName)
                .with(AzureVirtualMachineParameters::setOsUserPassword, osUserPassword)
                .with(AzureVirtualMachineParameters::setUserData, userData)
                .with(AzureVirtualMachineParameters::setVirtualMachineName, virtualMachineName)
                .with(AzureVirtualMachineParameters::setSize, flavorName)
                .build();

        this.azureVirtualMachineRequest.doCreateAsynchronously(azureVirtualMachineParameters, azureCloudUser);

        String instanceId = AzureResourceNameUtil.createVirtualMachineName(computeOrder);
        return instanceId;
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
