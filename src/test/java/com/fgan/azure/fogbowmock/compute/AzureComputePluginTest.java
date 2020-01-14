package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.AzureTestUtils;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.AzureStateMapper;
import com.fgan.azure.fogbowmock.common.Messages;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetImageRef;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.image.AzureImageOperationUtil;
import com.fgan.azure.fogbowmock.util.AzureConstants;
import com.fgan.azure.fogbowmock.util.AzureGeneralPolicy;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import com.fgan.azure.fogbowmock.util.AzureInstancePolicy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AzureGeneralPolicy.class, AzureImageOperationUtil.class, AzureInstancePolicy.class})
public class AzureComputePluginTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AzureComputePlugin azureComputePlugin;
    private AzureCloudUser azureCloudUser;
    private String defaultNetworkInterfaceName;
    private AzureVirtualMachineOperation azureVirtualMachineOperation;

    @Before
    public void setUp() {
        String azureConfFilePath = AzureTestUtils.AZURE_CONF_FILE_PATH;
        Properties properties = PropertiesUtil.readProperties(azureConfFilePath);
        this.defaultNetworkInterfaceName = properties.getProperty(AzureConstants.DEFAULT_NETWORK_INTERFACE_NAME_KEY);
        this.azureComputePlugin = Mockito.spy(new AzureComputePlugin(azureConfFilePath));
        this.azureVirtualMachineOperation = Mockito.mock(AzureVirtualMachineOperationSDK.class);
        this.azureComputePlugin.setAzureVirtualMachineOperation(this.azureVirtualMachineOperation);
        this.azureCloudUser = AzureTestUtils.createAzureCloudUser();
    }

    // test case: When calling the getNetworkInterfaceId method without network in the order,
    // it must verify if It returns the default networkInterfaceId.
    @Test
    public void testGetNetworkInterfaceIdSuccessfullyWhenEmptyNetworkInOrder() throws FogbowException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        List<String> networds = new ArrayList<>();
        Mockito.when(computeOrder.getNetworkIds()).thenReturn(networds);

        String networkInsterfaceIdExpected = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildNetworkInterfaceId(this.defaultNetworkInterfaceName);

        // exercise
        String networkInterfaceId = this.azureComputePlugin.getNetworkInterfaceId(computeOrder, this.azureCloudUser);

        // verify
        Assert.assertEquals(networkInsterfaceIdExpected, networkInterfaceId);
    }

    // test case: When calling the getNetworkInterfaceId method with one network in the order,
    // it must verify if It returns the network in the order.
    @Test
    public void testGetNetworkInterfaceIdSuccessfullyWhenOneNetworkInOrder() throws FogbowException {
        // set up
        String nertworkIdExpeceted = "networkId";
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        List<String> networds = new ArrayList<>();
        networds.add(nertworkIdExpeceted);
        Mockito.when(computeOrder.getNetworkIds()).thenReturn(networds);

        // exercise
        String networkInterfaceId = this.azureComputePlugin
                .getNetworkInterfaceId(computeOrder, this.azureCloudUser);

        // verify
        Assert.assertEquals(nertworkIdExpeceted, networkInterfaceId);
    }

    // test case: When calling the getNetworkInterfaceId method with more than one network in the order,
    // it must verify if It throws a FogbowException.
    @Test
    public void testGetNetworkInterfaceIdFailWhenMoreThanOneNetworkInOrder() throws FogbowException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        List<String> networds = Arrays.asList("one", "two");
        Mockito.when(computeOrder.getNetworkIds()).thenReturn(networds);

        // verify
        this.expectedException.expect(FogbowException.class);
        this.expectedException.expectMessage(Messages.MULTIPLE_NETWORKS_NOT_ALLOWED);

        // exercise
        this.azureComputePlugin.getNetworkInterfaceId(computeOrder, this.azureCloudUser);

    }

    // test case: When calling the getInstance method, it must verify if It returns the right computeInstance.
    @Test
    public void testGetInstanceSuccessfully() throws FogbowException {
        // set up
        ComputeOrder computeOrder = new ComputeOrder();
        String instanceId = "instanceId";
        computeOrder.setInstanceId(instanceId);

        AzureGetVirtualMachineRef azureGetVirtualMachineRef = Mockito.mock(AzureGetVirtualMachineRef.class);
        Mockito.when(this.azureVirtualMachineOperation
                .doGetInstance(Mockito.eq(instanceId), Mockito.eq(this.azureCloudUser)))
                .thenReturn(azureGetVirtualMachineRef);

        ComputeInstance computeInstanceExpected = Mockito.mock(ComputeInstance.class);
        Mockito.doReturn(computeInstanceExpected).when(this.azureComputePlugin)
                .buildComputeInstance(Mockito.eq(azureGetVirtualMachineRef), Mockito.eq(this.azureCloudUser));

        // exercise
        ComputeInstance computeInstance = this.azureComputePlugin.getInstance(computeOrder, this.azureCloudUser);

        // verify
        Assert.assertEquals(computeInstanceExpected, computeInstance);
    }

    // test case: When calling the getInstance method and throws a Exception,
    // it must verify if It does not treat and rethrow the same exception.
    @Test
    public void testGetInstanceFail() throws FogbowException {
        // set up
        ComputeOrder computeOrder = new ComputeOrder();
        String instanceId = "instanceId";
        computeOrder.setInstanceId(instanceId);

        Mockito.when(this.azureVirtualMachineOperation
                .doGetInstance(Mockito.eq(instanceId), Mockito.eq(this.azureCloudUser)))
                .thenThrow(new UnexpectedException());

        // verify
        this.expectedException.expect(UnexpectedException.class);

        // exercise
        this.azureComputePlugin.getInstance(computeOrder, this.azureCloudUser);
    }

    // test case: When calling the buildComputeInstance method,
    // it must verify if it returns the right ComputeInstance.
    @Test
    public void testBuildComputeInstanceSuccessfully() {
        // set up
        int diskExpected = 1;
        String nameExpected = "name";
        int memoryExpected = 2;
        int vcpuExpected = 3;
        String cloudStateExpected = AzureStateMapper.SUCCEEDED_STATE;
        List<String> ipAddressExpected = Arrays.asList("id");
        String idExpected = AzureIdBuilder.configure(this.azureCloudUser).buildVirtualMachineId(nameExpected);
        AzureGetVirtualMachineRef azureGetVirtualMachineRef = AzureGetVirtualMachineRef.builder()
                .disk(diskExpected)
                .vCPU(vcpuExpected)
                .memory(memoryExpected)
                .cloudState(cloudStateExpected)
                .name(nameExpected)
                .ipAddresses(ipAddressExpected)
                .build();

        // exercise
        ComputeInstance computeInstance = this.azureComputePlugin
                .buildComputeInstance(azureGetVirtualMachineRef, this.azureCloudUser);

        // verify
        Assert.assertEquals(diskExpected, computeInstance.getDisk());
        Assert.assertEquals(memoryExpected, computeInstance.getMemory());
        Assert.assertEquals(vcpuExpected, computeInstance.getvCPU());
        Assert.assertEquals(idExpected, computeInstance.getId());
        Assert.assertEquals(cloudStateExpected, computeInstance.getCloudState());
        Assert.assertEquals(ipAddressExpected, computeInstance.getIpAddresses());
    }

    // test case: When calling the requestInstance method with mocked methods,
    // it must verify if it creates all variable correct.
    @Test
    public void testRequestInstanceSuccessfully() throws FogbowException {
        // set up
        String imageId = "imageId";
        String orderId = "orderId";
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        Mockito.when(computeOrder.getImageId()).thenReturn(imageId);
        Mockito.when(computeOrder.getId()).thenReturn(orderId);

        String networkInterfaceId = "networkInterfaceId";
        Mockito.doReturn(networkInterfaceId).when(this.azureComputePlugin)
                .getNetworkInterfaceId(Mockito.eq(computeOrder), Mockito.eq(this.azureCloudUser));

        String virtualMachineSizeName = "virtualMachineSizeName";
        Mockito.doReturn(virtualMachineSizeName).when(this.azureComputePlugin)
                .getVirtualMachineSizeName(Mockito.eq(computeOrder), Mockito.eq(this.azureCloudUser));

        PowerMockito.mockStatic(AzureGeneralPolicy.class);
        int disk = 1;
        Mockito.when(AzureGeneralPolicy.getDisk(Mockito.eq(computeOrder))).thenReturn(disk);

        AzureGetImageRef azureGetImageRef = new AzureGetImageRef("", "", "");
        PowerMockito.mockStatic(AzureImageOperationUtil.class);
        Mockito.when(AzureImageOperationUtil.buildAzureVirtualMachineImageBy(Mockito.eq(imageId)))
                .thenReturn(azureGetImageRef);

        String virtualMachineName = "virtualMachineName";
        PowerMockito.mockStatic(AzureInstancePolicy.class);
        PowerMockito.when(AzureInstancePolicy.
                generateAzureResourceNameBy(Mockito.eq(computeOrder), Mockito.eq(this.azureCloudUser)))
                .thenReturn(virtualMachineName);

        String userData = "userData";
        Mockito.doReturn(userData).when(this.azureComputePlugin).getUserData();

        String password = "password";
        PowerMockito.when(AzureGeneralPolicy.generatePassword()).thenReturn(password);

        String regionName = this.azureCloudUser.getRegionName();
        String resourceGroupName = this.azureCloudUser.getResourceGroupName();

        AzureCreateVirtualMachineRef azureCreateVirtualMachineRef = AzureCreateVirtualMachineRef.builder()
                .virtualMachineName(virtualMachineName)
                .azureGetImageRef(azureGetImageRef)
                .networkInterfaceId(networkInterfaceId)
                .diskSize(disk)
                .size(virtualMachineSizeName)
                .osComputeName(orderId)
                .osUserName(orderId)
                .osUserPassword(password)
                .regionName(regionName)
                .resourceGroupName(resourceGroupName)
                .userData(userData)
                .checkAndBuild();


        // exercise
        this.azureComputePlugin.requestInstance(computeOrder, this.azureCloudUser);

        // verify
        Mockito.verify(this.azureComputePlugin, Mockito.times(1)).doRequestInstance(
                Mockito.eq(computeOrder), Mockito.eq(this.azureCloudUser), Mockito.eq(azureCreateVirtualMachineRef)
        );
    }

    // test case: When calling the requestInstance method any throws any exception,
    // it must verify if it re-throws the exception.
    @Test
    public void testRequestInstanceFail() throws FogbowException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);

        Mockito.doThrow(FogbowException.class).when(this.azureComputePlugin)
                .getNetworkInterfaceId(Mockito.eq(computeOrder), Mockito.eq(this.azureCloudUser));

        // verify
        this.expectedException.expect(FogbowException.class);

        // exercise
        this.azureComputePlugin.requestInstance(computeOrder, this.azureCloudUser);
    }

    // test case: When calling the getVirtualMachineSizeName method,
    // it must verify if it calls the method with right parameters.
    @Test
    public void testGetVirtualMachineSizeName() throws FogbowException {
        // set up
        int memory = 1;
        int vcpu = 1;
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        Mockito.when(computeOrder.getMemory()).thenReturn(memory);
        Mockito.when(computeOrder.getvCPU()).thenReturn(vcpu);

        String regionName = this.azureCloudUser.getRegionName();

        // exercise
        this.azureComputePlugin.getVirtualMachineSizeName(computeOrder, this.azureCloudUser);

        // verify
        Mockito.verify(this.azureVirtualMachineOperation, Mockito.times(1)).findVirtualMachineSizeName(
                Mockito.eq(memory), Mockito.eq(vcpu), Mockito.eq(regionName), Mockito.eq(this.azureCloudUser)
        );
    }

    // test case: When calling the deleteInstance method,
    // it must verify if it calls the method with right parameters.
    @Test
    public void testDeleteInstanceSuccessfully() throws FogbowException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        String instanceId = "instanceId";
        Mockito.when(computeOrder.getInstanceId()).thenReturn(instanceId);

        Mockito.doNothing()
                .when(this.azureVirtualMachineOperation)
                .doDeleteInstance(Mockito.any(), Mockito.any());

        // exercise
        this.azureComputePlugin.deleteInstance(computeOrder, this.azureCloudUser);

        // verify
        Mockito.verify(this.azureVirtualMachineOperation, Mockito.times(1))
                .doDeleteInstance(Mockito.eq(instanceId), Mockito.eq(this.azureCloudUser));
    }

}
