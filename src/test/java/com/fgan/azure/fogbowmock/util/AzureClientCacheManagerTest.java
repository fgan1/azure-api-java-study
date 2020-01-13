package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import com.fgan.azure.AzureTestUtils;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AzureClientCacheManager.class})
public class AzureClientCacheManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private AzureCloudUser azureCloudUser;

    @Before
    public void setUp() {
        this.azureCloudUser = AzureTestUtils.createAzureCloudUser();
    }

    // test case: When calling the getAzure method and throws an exception,
    // it must verify if It throws an UnauthenticatedUserException.
    @Test
    public void testGetAzureFail() throws Exception {
        // set up
        PowerMockito.spy(AzureClientCacheManager.class);

        PowerMockito.doThrow(new FogbowException())
                .when(AzureClientCacheManager.class, "createAzure", Mockito.eq(this.azureCloudUser));

        // verify
        this.expectedException.expect(UnauthenticatedUserException.class);

        // exercise
        AzureClientCacheManager.getAzure(this.azureCloudUser);
    }

    // test case: When calling the createAzure method and occurs any error,
    // it must verify if It throws a FogbowException.
    @Test
    public void testCreateAzure() throws FogbowException {

        AzureCloudUser azureCloudUserUnauthorized = new AzureCloudUser(null,null,null,
                null,null,null,null,null);

        // verify
        this.expectedException.expect(FogbowException.class);

        // exercise
        AzureClientCacheManager.createAzure(azureCloudUserUnauthorized);
    }

    // test case: When calling the buildAzureTokenCredentials method,
    // it must verify if It creates a right AzureTokenCredentials.
    @Test
    public void testBuildAzureTokenCredentialsSuccessfully() {
        // set up
        PowerMockito.spy(AzureClientCacheManager.class);

        String clientId = this.azureCloudUser.getClientId();
        String tenantId = this.azureCloudUser.getTenantId();
        String clientKey = this.azureCloudUser.getClientKey();
        String subscriptionId = this.azureCloudUser.getSubscriptionId();

        final String managementEndpoint = AzureEnvironment.AZURE.managementEndpoint();
        final String activeDirectoryEndpoint = AzureEnvironment.AZURE.activeDirectoryEndpoint();
        final String resourceManagerEndpoint = AzureEnvironment.AZURE.resourceManagerEndpoint();
        final String graphEndpoint = AzureEnvironment.AZURE.graphEndpoint();
        final String keyVaultDnsSuffix = AzureEnvironment.AZURE.keyVaultDnsSuffix();

        // exercise
        AzureTokenCredentials azureTokenCredentials =
                AzureClientCacheManager.buildAzureTokenCredentials(this.azureCloudUser);

        // verify
        AzureEnvironment environment = azureTokenCredentials.environment();
        Assert.assertEquals(subscriptionId, azureTokenCredentials.defaultSubscriptionId());
        Assert.assertEquals(graphEndpoint, environment.graphEndpoint());
        Assert.assertEquals(managementEndpoint, environment.managementEndpoint());
        Assert.assertEquals(activeDirectoryEndpoint, environment.activeDirectoryEndpoint());
        Assert.assertEquals(resourceManagerEndpoint, environment.resourceManagerEndpoint());
        Assert.assertEquals(keyVaultDnsSuffix, environment.keyVaultDnsSuffix());
        PowerMockito.verifyStatic(AzureClientCacheManager.class, VerificationModeFactory.times(1));
        AzureClientCacheManager.getApplicationTokenCredentials(Mockito.eq(clientId),
                Mockito.eq(tenantId), Mockito.eq(clientKey), Mockito.any());
    }

}
