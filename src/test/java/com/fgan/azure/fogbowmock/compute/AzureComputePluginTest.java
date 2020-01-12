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
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.util.AzureConstants;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AzureComputePluginTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AzureComputePlugin azureComputePlugin;
    private AzureCloudUser azureCloudUser;
    private String defaultNetworkInterfaceName;
    private AzureVirtualMachineOperation<AzureVirtualMachineOperationSDK> azureVirtualMachineOperation;

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
    // it must verify if it retuns the right ComputeInstance.
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

}
