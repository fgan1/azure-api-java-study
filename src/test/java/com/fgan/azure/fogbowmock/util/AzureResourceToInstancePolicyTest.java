package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.models.orders.Order;
import com.fgan.azure.AzureTestUtils;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.BiFunction;

public class AzureResourceToInstancePolicyTest {

    private AzureCloudUser azureCloudUser;

    @Before
    public void setUp() {
        this.azureCloudUser = AzureTestUtils.createAzureCloudUser();
    }

    // test case: When calling the generateAzureResourceNameBy method with general order,
    // it must verify if it returns a resourceGroupName using the order id.
    @Test
    public void testGenerateAzureResourceNameSuccessfullyByWhenOrder() throws InvalidParameterException {
        // set up
        Order order = Mockito.mock(Order.class);
        String orderId = "orderId";
        Mockito.when(order.getId()).thenReturn(orderId);

        String resourceNameExpected = SystemConstants.FOGBOW_INSTANCE_NAME_PREFIX + orderId;

        // exercise
        String resourceName = AzureResourceToInstancePolicy.generateAzureResourceNameBy(order.getId(),
                this.azureCloudUser);

        // verify
        Assert.assertEquals(resourceNameExpected, resourceName);
    }

    // test case: When calling the generateAzureResourceNameBy method with Compute order,
    // it must verify if it returns a resourceGroupName using the order name.
    @Test
    public void testGenerateAzureResourceNameSuccessfullyByWhenComputeOrder() throws InvalidParameterException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        String orderId = "orderId";
        Mockito.when(computeOrder.getId()).thenReturn(orderId);
        String resourceNameExpected = "resourceName";
        Mockito.when(computeOrder.getName()).thenReturn(resourceNameExpected);

        // exercise
        String resourceName = AzureResourceToInstancePolicy.generateAzureResourceNameBy(computeOrder, this.azureCloudUser);

        // verify
        Assert.assertEquals(resourceNameExpected, resourceName);
    }

    // test case: When calling the generateFogbowInstanceIdBy method with Compute order,
    // it must verify if it returns an instance using the order name.
    @Test
    public void testGenerateFogbowInstanceIdBySuccessfullyWhenComputeOrder()
            throws InvalidParameterException {

        // set up
        String resourceName = "resourceName";
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        String orderId = "orderId";
        Mockito.when(computeOrder.getId()).thenReturn(orderId);
        Mockito.when(computeOrder.getName()).thenReturn(resourceName);
        BiFunction<String, AzureCloudUser, String> builder = (name, cloudUser) ->
                AzureIdBuilder.configure(cloudUser).buildVirtualMachineId(name);

        String instanceIdExpected = AzureIdBuilder.configure(this.azureCloudUser)
                .buildVirtualMachineId(resourceName);

        // exercise
        String instanceId = AzureResourceToInstancePolicy
                .generateFogbowInstanceIdBy(computeOrder, this.azureCloudUser, builder);

        // verify
        Assert.assertEquals(instanceIdExpected, instanceId);
    }

    // test case: When calling the generateFogbowInstanceIdBy method with general order,
    // it must verify if it returns an instance using the order name.
    @Test
    public void testGenerateFogbowInstanceIdBySuccessfullyWhenOrder()
            throws InvalidParameterException {

        // set up
        Order order = Mockito.mock(Order.class);
        String orderId = "orderId";
        Mockito.when(order.getId()).thenReturn(orderId);
        BiFunction<String, AzureCloudUser, String> builder = (name, cloudUser) ->
                AzureIdBuilder.configure(cloudUser).buildVirtualMachineId(name);

        String resourceNameExpected = SystemConstants.FOGBOW_INSTANCE_NAME_PREFIX + orderId;

        String instanceIdExpected = AzureIdBuilder.configure(this.azureCloudUser)
                .buildVirtualMachineId(resourceNameExpected);

        // exercise
        String instanceId = AzureResourceToInstancePolicy
                .generateFogbowInstanceIdBy(order.getId(), this.azureCloudUser, builder);

        // verify
        Assert.assertEquals(instanceIdExpected, instanceId);
    }


}
