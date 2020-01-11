package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.common.Messages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class AzureGeneralPolicyTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // test case: When calling the getDisk method with disk value valid in the compute order,
    // it must verify if it returns the disk value.
    @Test
    public void testGetDiskSuccessfully() throws InvalidParameterException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        int diskExpected = AzureGeneralPolicy.MINIMUM_DISK;
        Mockito.when(computeOrder.getDisk()).thenReturn(diskExpected);

        // exercise
        int disk = AzureGeneralPolicy.getDisk(computeOrder);

        // verify
        Assert.assertEquals(diskExpected, disk);
    }

    // test case: When calling the getDisk method with disk value invalid in the compute order,
    // it must verify if it throws an excepyion.
    @Test
    public void testGetDiskFail() throws InvalidParameterException {
        // set up
        ComputeOrder computeOrder = Mockito.mock(ComputeOrder.class);
        int diskInvalid = AzureGeneralPolicy.MINIMUM_DISK - 1;
        Mockito.when(computeOrder.getDisk()).thenReturn(diskInvalid);

        // verify
        this.expectedException.expect(InvalidParameterException.class);
        this.expectedException.expectMessage(Messages.DISK_PARAMETER_AZURE_POLICY);

        // exercise
        AzureGeneralPolicy.getDisk(computeOrder);
    }

}
