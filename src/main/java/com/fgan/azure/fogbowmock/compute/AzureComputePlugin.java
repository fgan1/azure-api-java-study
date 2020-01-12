package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.*;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.ComputePlugin;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.AzureStateMapper;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.image.AzureImageOperationUtil;
import com.fgan.azure.fogbowmock.util.AzureConstants;
import com.fgan.azure.fogbowmock.util.AzureGeneralPolicy;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import com.fgan.azure.fogbowmock.util.AzureResourceToInstancePolicy;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;

public class AzureComputePlugin implements ComputePlugin<AzureCloudUser> {

    private static final Logger LOGGER = Logger.getLogger(AzureComputePlugin.class);
    private final AzureVirtualMachineOperation<AzureVirtualMachineOperationSDK> azureVirtualMachineOperation;
    //    private final DefaultLaunchCommandGenerator launchCommandGenerator;
    private final String defaultNetworkInterfaceName;

    public AzureComputePlugin(String confFilePath) {
        Properties properties = PropertiesUtil.readProperties(confFilePath);

        this.defaultNetworkInterfaceName = properties.getProperty(AzureConstants.DEFAULT_NETWORK_INTERFACE_NAME_KEY);
//        this.launchCommandGenerator = new DefaultLaunchCommandGenerator();
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
        String virtualMachineSizeName = getVirtualMachineSizeName(computeOrder, azureCloudUser);
        int diskSize = AzureGeneralPolicy.getDisk(computeOrder);
        AzureGetImageRef azureVirtualMachineImage = AzureImageOperationUtil
                .buildAzureVirtualMachineImageBy(computeOrder.getImageId());
        String virtualMachineName = AzureResourceToInstancePolicy
                .generateAzureResourceNameBy(computeOrder, azureCloudUser);
        String userData = getUserData();
        String osUserName = computeOrder.getId();
        String osUserPassword = AzureGeneralPolicy.generatePassword();
        String osComputeName = computeOrder.getId();
        String regionName = azureCloudUser.getRegionName();
        String resourceGroupName = azureCloudUser.getResourceGroupName();

        AzureCreateVirtualMachineRef azureCreateVirtualMachineRef = AzureCreateVirtualMachineRef.builder()
                .virtualMachineName(virtualMachineName)
                .azureVirtualMachineImage(azureVirtualMachineImage)
                .networkInterfaceId(networkInterfaceId)
                .diskSize(diskSize)
                .size(virtualMachineSizeName)
                .osComputeName(osComputeName)
                .osUserName(osUserName)
                .osUserPassword(osUserPassword)
                .regionName(regionName)
                .resourceGroupName(resourceGroupName)
                .userData(userData)
                .checkAndBuild();

        return doRequestInstance(computeOrder, azureCloudUser, azureCreateVirtualMachineRef);
    }

    private String getVirtualMachineSizeName(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        return this.azureVirtualMachineOperation.findVirtualMachineSize(
                computeOrder.getMemory(), computeOrder.getvCPU(),
                azureCloudUser.getRegionName(), azureCloudUser);
    }

    @VisibleForTesting
    String doRequestInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser,
                             AzureCreateVirtualMachineRef azureCreateVirtualMachineRef)
            throws UnauthenticatedUserException, UnexpectedException, InstanceNotFoundException, InvalidParameterException {

        this.azureVirtualMachineOperation.doCreateInstance(azureCreateVirtualMachineRef, azureCloudUser);
        return AzureResourceToInstancePolicy.generateFogbowInstanceIdBy(computeOrder, azureCloudUser,
                (name, cloudUser) -> AzureIdBuilder.configure(cloudUser).buildVirtualMachineId(name));
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
        if (!networkIds.isEmpty()) {
            if (networkIds.size() > 1)
                throw new FogbowException("Multiple networks not allowed yed");

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
        String azureVirtualMachineId = computeOrder.getInstanceId();

        AzureGetVirtualMachineRef azureGetVirtualMachineRef = this.azureVirtualMachineOperation
                .doGetInstance(azureVirtualMachineId, azureCloudUser);

        return buildComputeInstance(azureGetVirtualMachineRef);
    }

    @VisibleForTesting
    ComputeInstance buildComputeInstance(AzureGetVirtualMachineRef azureGetVirtualMachineRef) {
        // TODO(chico) - check if is necessary put the real ID(Azure)
        String id = azureGetVirtualMachineRef.getId();
        String cloudState = azureGetVirtualMachineRef.getCloudState();
        String name = azureGetVirtualMachineRef.getName();
        int vCPU = azureGetVirtualMachineRef.getvCPU();
        int memory = azureGetVirtualMachineRef.getMemory();
        int disk = azureGetVirtualMachineRef.getDisk();
        List<String> ipAddresses = azureGetVirtualMachineRef.getIpAddresses();

        return new ComputeInstance(id, cloudState, name, vCPU, memory, disk, ipAddresses);
    }

    @Override
    public void deleteInstance(ComputeOrder computeOrder, AzureCloudUser azureCloudUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Info.DELETING_INSTANCE_S, computeOrder.getInstanceId()));

        String azureVirtualMachineId = computeOrder.getInstanceId();
        this.azureVirtualMachineOperation.doDeleteInstance(azureVirtualMachineId, azureCloudUser);
    }

}
