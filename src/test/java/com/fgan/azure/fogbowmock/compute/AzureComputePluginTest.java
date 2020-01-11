package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.AzureTestUtils;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureGetVirtualMachineRef;
import com.fgan.azure.fogbowmock.util.AzureConstants;
import com.fgan.azure.fogbowmock.util.AzureIdBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AzureComputePluginTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AzureComputePlugin azureComputePlugin;
    private AzureCloudUser azureCloudUser;
    private String defaultNetworkInterfaceName;

    @Before
    public void setUp() {
        String azureConfFilePath = AzureTestUtils.AZURE_CONF_FILE_PATH;
        Properties properties = PropertiesUtil.readProperties(azureConfFilePath);
        this.defaultNetworkInterfaceName = properties.getProperty(AzureConstants.DEFAULT_NETWORK_INTERFACE_NAME_KEY);
//        this.resourceGroupName = this.properties.getProperty(DEFAULT_RESOURCE_GROUP_NAME_KEY);
//        this.regionName = this.properties.getProperty(DEFAULT_REGION_NAME_KEY);
        this.azureComputePlugin = Mockito.spy(new AzureComputePlugin(azureConfFilePath));
        this.azureCloudUser = AzureTestUtils.createAzureCloudUser();
    }

    // test case: When calling the getInstance method, it must verify if It returns the right computeInstance.
    @Test
    public void testGetInstanceSuccessfully() throws FogbowException {
        // set up
        ComputeOrder computeOrder = new ComputeOrder();
        String instanceId = "instanceId";
        computeOrder.setInstanceId(instanceId);

        String azureVirtualMachineId = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildVirtualMachineId(computeOrder.getInstanceId());

        AzureGetVirtualMachineRef azureGetVirtualMachineRef = Mockito.mock(AzureGetVirtualMachineRef.class);
        Mockito.doReturn(azureGetVirtualMachineRef).when(this.azureComputePlugin)
                .doRequestInstance(Mockito.eq(this.azureCloudUser), Mockito.eq(azureVirtualMachineId));

        ComputeInstance computeInstanceExpected = Mockito.mock(ComputeInstance.class);
        Mockito.doReturn(computeInstanceExpected).when(this.azureComputePlugin)
                .buildComputeInstance(Mockito.eq(azureGetVirtualMachineRef));

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

        String azureVirtualMachineId = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildVirtualMachineId(computeOrder.getInstanceId());

        Mockito.doThrow(new FogbowException()).when(this.azureComputePlugin)
                .doRequestInstance(Mockito.eq(this.azureCloudUser), Mockito.eq(azureVirtualMachineId));

        // verify
        this.expectedException.expect(FogbowException.class);

        // exercise
        this.azureComputePlugin.getInstance(computeOrder, this.azureCloudUser);
    }

    // test case: When calling the getNetworkInterfaceId method without networks in the order,
    // it must verify if It returns the rigth networkInterfaceId.
    @Test
    public void testGetNetworkInterfaceIdSuccessfullyWhenGetNetworkDefault() throws FogbowException {
        // set up
        ComputeOrder computeOrder = new ComputeOrder();

        String networkInterfaceIdExpected = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildNetworkInterfaceId(this.defaultNetworkInterfaceName);

        // verify
        String networkInterfaceId = this.azureComputePlugin.getNetworkInterfaceId(computeOrder, this.azureCloudUser);

        // exercise
        Assert.assertEquals(networkInterfaceIdExpected, networkInterfaceId);
    }

    // test case: When calling the getNetworkInterfaceId method with one network in the order,
    // it must verify if It returns the rigth networkInterfaceId.
    @Test
    public void testGetNetworkInterfaceIdSuccessfullyWhenGetUserNetwork() throws FogbowException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        String fogbowNetworkInterfaceIdPassedByUser = "networkInterfaceId";
        List<String> networks = Arrays.asList(fogbowNetworkInterfaceIdPassedByUser);
        Mockito.when(computeOrder.getNetworkIds()).thenReturn(networks);

        String networkInterfaceIdExpected = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildNetworkInterfaceId(fogbowNetworkInterfaceIdPassedByUser);

        // verify
        String networkInterfaceId = this.azureComputePlugin.getNetworkInterfaceId(computeOrder, this.azureCloudUser);

        // exercise
        Assert.assertEquals(networkInterfaceIdExpected, networkInterfaceId);
    }

    // TODO (chico) - review it
    // test case: When calling the getNetworkInterfaceId method with many networks in the order,
    // it must verify if It throws an .
    @Test
    public void testGetNetworkInterfaceIdFail() throws FogbowException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        List<String> networks = Arrays.asList("", "");
        Mockito.when(computeOrder.getNetworkIds()).thenReturn(networks);

        // verify
        this.expectedException.expect(FogbowException.class);

        // verify
        this.azureComputePlugin.getNetworkInterfaceId(computeOrder, this.azureCloudUser);
    }

}
