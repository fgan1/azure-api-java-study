package com.fgan.azure.fogbowmock.common;

import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.core.models.ResourceType;
import org.junit.Assert;
import org.junit.Test;

public class AzureStateMapperTest {

    // test case: When calling the map method with compute type and creating state,
    // it must verify if It returns the instance creating state.
    @Test
    public void testMapSuccessfullyWhenCreatingState() {
        // set up
        ResourceType resourceType = ResourceType.COMPUTE;

        // exercise
        InstanceState instanceState = AzureStateMapper.map(resourceType, AzureStateMapper.CREATING_STATE);

        // verify
        Assert.assertEquals(InstanceState.CREATING, instanceState);
    }

    // test case: When calling the map method with compute type and creating state,
    // it must verify if It returns the instance creating state.
    @Test
    public void testMapSuccessfullyWhenSuccededState() {
        // set up
        ResourceType resourceType = ResourceType.COMPUTE;

        // exercise
        InstanceState instanceState = AzureStateMapper.map(resourceType, AzureStateMapper.SUCCEEDED_STATE);

        // verify
        Assert.assertEquals(InstanceState.READY, instanceState);
    }

    // test case: When calling the map method with compute type and creating state,
    // it must verify if It returns the instance creating state.
    @Test
    public void testMapSuccessfullyWhenUndefinedState() {
        // set up
        ResourceType resourceType = ResourceType.COMPUTE;

        // exercise
        InstanceState instanceState = AzureStateMapper.map(resourceType, "undefined");

        // verify
        Assert.assertEquals(InstanceState.INCONSISTENT, instanceState);
    }

}
