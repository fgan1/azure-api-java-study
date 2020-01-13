package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.core.models.orders.Order;
import com.fgan.azure.AzureTestUtils;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.Messages;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AzureIdBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private AzureCloudUser azureCloudUser;

    @Before
    public void setUp() {
        this.azureCloudUser = AzureTestUtils.createAzureCloudUser();
    }

    // test case: When calling the buildNetworkInterfaceId method,
    // it must verify if It return the right value.
    @Test
    public void testBuildNetworkInterfaceIdSuccessfully() {
        // set up
        String networkInterfaceName = "networkInterfaceName";

        String networkInterfaceIdExpected = String.format(AzureIdBuilder.NETWORK_INTERFACE_STRUCTURE,
                this.azureCloudUser.getSubscriptionId(),
                this.azureCloudUser.getResourceGroupName(),
                networkInterfaceName);

        // exercise
        String networkInterfaceId = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildNetworkInterfaceId(networkInterfaceName);

        // verify
        Assert.assertEquals(networkInterfaceIdExpected, networkInterfaceId);
    }

    // test case: When calling the buildVirtualMachineId method,
    // it must verify if It return the right value.
    @Test
    public void testBuildVirtualMachineIdSuccessfully() {
        // set up
        String virtualMachineName = "virtualMachineName";

        String networkInterfaceIdExpected = String.format(AzureIdBuilder.VIRTUAL_MACHINE_STRUCTURE,
                this.azureCloudUser.getSubscriptionId(),
                this.azureCloudUser.getResourceGroupName(),
                virtualMachineName);

        // exercise
        String networkInterfaceId = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildVirtualMachineId(virtualMachineName);

        // verify
        Assert.assertEquals(networkInterfaceIdExpected, networkInterfaceId);
    }


    // test case: When calling the checkIdSizePolicy method with resourceName within the limit,
    // it must verify if It does not throw an InvalidParameterException.
    @Test
    public void testCheckIdSizePolicySuccessfully() throws InvalidParameterException {
        // set up
        int whatLeftToTotalLimit = getWhatLeftToTotalLimit();
        String resourceNameInTheLimit = String.valueOf(new char[whatLeftToTotalLimit]);

        String idInTheLimitSize = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildNetworkInterfaceId(resourceNameInTheLimit);

        // exercise
        AzureIdBuilder
                .configure(this.azureCloudUser)
                .checkIdSizePolicy(resourceNameInTheLimit);

        // verify
        Assert.assertEquals(Order.FIELDS_MAX_SIZE, idInTheLimitSize.length());
    }

    // test case: When calling the checkIdSizePolicy method with resourceName out of the limit,
    // it must verify if It throws an InvalidParameterException.
    @Test
    public void testCheckIdSizePolicyFail() throws InvalidParameterException {
        // set up
        int whatLeftToTotalLimit = getWhatLeftToTotalLimit();
        int outOfLimit = 1;
        String resourceNameInTheLimit = String.valueOf(new char[whatLeftToTotalLimit + outOfLimit]);

        String idInTheLimitSize = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildNetworkInterfaceId(resourceNameInTheLimit);

        // verify
        Assert.assertEquals(Order.FIELDS_MAX_SIZE + outOfLimit, idInTheLimitSize.length());
        this.expectedException.expect(InvalidParameterException.class);
        this.expectedException.expectMessage(String.format(Messages.ERROR_ID_LIMIT_SIZE_EXCEEDED, outOfLimit));

        // exercise
        AzureIdBuilder
                .configure(this.azureCloudUser)
                .checkIdSizePolicy(resourceNameInTheLimit);

    }

    private int getWhatLeftToTotalLimit() {
        String anyIdEmpty = AzureIdBuilder
                .configure(this.azureCloudUser)
                .buildId(AzureIdBuilder.BIGGER_STRUCTURE, "");
        return Order.FIELDS_MAX_SIZE - anyIdEmpty.length();
    }

}
