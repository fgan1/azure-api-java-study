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
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.image.AzureImageOperation;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import com.fgan.azure.fogbowmock.util.AzureResourceToInstancePolicy;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;

public class AzureComputePlugin implements ComputePlugin<AzureCloudUser> {

    private static final Logger LOGGER = Logger.getLogger(AzureComputePlugin.class);

    private static final String DEFAULT_NETWORK_INTERFACE_NAME_KEY = "default_network_interface_name";
    private static final String DEFAULT_RESOURCE_GROUP_NAME_KEY = "resource_group_name";
    private static final String DEFAULT_REGION_NAME_KEY = "region_name";

    private final AzureVirtualMachineOperation<AzureVirtualMachineOperationSDK> azureVirtualMachineOperation;
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
        this.azureVirtualMachineOperation = new AzureVirtualMachineOperationSDK();
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
        String flavorName = this.azureVirtualMachineOperation.findVirtualMachineSizeName(
                computeOrder.getMemory(), computeOrder.getvCPU(), azureCloudUser);
        int diskSize = computeOrder.getDisk();
        AzureGetImageRef azureVirtualMachineImage = AzureImageOperation.buildAzureVirtualMachineImageBy(computeOrder.getImageId());
        String virtualMachineName = AzureResourceToInstancePolicy.generateAzureResourceNameBy(computeOrder);
        String userData = getUserData();
        String osUserName = computeOrder.getId();
        String osUserPassword = computeOrder.getId();
        String osComputeName = computeOrder.getId();

        AzureCreateVirtualMachineRef azureCreateVirtualMachineRef = AzureCreateVirtualMachineRef.builder()
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

        this.azureVirtualMachineOperation.doCreateInstance(azureCreateVirtualMachineRef, azureCloudUser);

        return AzureResourceToInstancePolicy.generateFogbowInstanceIdBy(computeOrder);
    }

    private String getUserData() {
//        return this.launchCommandGenerator.createLaunchCommand(computeOrder);
        // TODO(chico) - Remove when It goes to the Fogbow context
        return com.fgan.azure.util.PropertiesUtil.getUserData();
    }

    // TODO(chico) - Finish; Study multi network interfaces behaviour.
    @VisibleForTesting
    String getNetworkInterfaceId(ComputeOrder computeOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        String networkInterfaceId;
        List<String> networkIds = computeOrder.getNetworkIds();
        if (networkIds.size() > 1) {
            throw new FogbowException("Multiple networks not allowed yed");
        } else if (networkIds.size() == 1) {
            networkInterfaceId = networkIds.stream().findFirst().get();
        } else {
            networkInterfaceId = this.defaultNetworkInterfaceName;
        }

        return AzureIdBuilder
                .configure(azureCloudUser)
                .buildNetworkInterfaceId(networkInterfaceId);
    }

    @Override
    public ComputeInstance getInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Info.GETTING_INSTANCE_S, computeOrder.getInstanceId()));

        String azureVirtualMachineId = AzureIdBuilder
                .configure(azureCloudUser)
                .buildVirtualMachineId(computeOrder.getInstanceId());

        AzureGetVirtualMachineRef azureGetVirtualMachineRef =
                this.azureVirtualMachineOperation.doGetInstance(azureVirtualMachineId, azureCloudUser);

        return buildComputeInstance(azureGetVirtualMachineRef);
    }

    private ComputeInstance buildComputeInstance(AzureGetVirtualMachineRef azureGetVirtualMachineRef) {
        String id = azureGetVirtualMachineRef.getId();
        String cloudState = azureGetVirtualMachineRef.getCloudState();
        String name = azureGetVirtualMachineRef.getName();
        int vCPU = azureGetVirtualMachineRef.getvCPU();
        int memory = azureGetVirtualMachineRef.getMemory();
        int disk = azureGetVirtualMachineRef.getDisk();
        List<String> ipAddresses = azureGetVirtualMachineRef.getIpAddresses();

        return new ComputeInstance(id, cloudState, name, vCPU, memory, disk,ipAddresses);
    }

    @Override
    public void deleteInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Info.DELETING_INSTANCE_S, computeOrder.getInstanceId()));

        String azureVirtualMachineId = AzureIdBuilder
                .configure(azureCloudUser)
                .buildVirtualMachineId(computeOrder.getInstanceId());

        this.azureVirtualMachineOperation.doDeleteInstance(azureVirtualMachineId, azureCloudUser);
    }

}
