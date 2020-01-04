package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.FogbowException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.microsoft.azure.management.Azure;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class AzureVirtualMachineOperationSDKTest {

    private AzureVirtualMachineOperationSDK azureVirtualMachineOperationSDK;
    private AzureCloudUser azureCloudUser;
    private Azure azure;

    @Before
    public void setUp() throws AzureException.Unauthorized {
        this.azureVirtualMachineOperationSDK =
                Mockito.spy(new AzureVirtualMachineOperationSDK());
        this.azureCloudUser = Mockito.mock(AzureCloudUser.class);
        this.azure = Mockito.mock(Azure.class);
        mockGetAzureClient();
    }

    @Test
    public void testDoCreateInstance() throws AzureException.ResourceNotFound, AzureException.Unauthorized {
        // set up
        AzureCreateVirtualMachineRef azureCreateVirtualMachineRef = AzureCreateVirtualMachineRef.builder()
                .build();

        // exercise
        this.azureVirtualMachineOperationSDK.doCreateInstance(azureCreateVirtualMachineRef, this.azureCloudUser);

        // verify
    }

    private void mockGetAzureClient() throws AzureException.Unauthorized {
        PowerMockito.mockStatic(AzureClientCacheManager.class);
        PowerMockito.doReturn(this.azure)
                .when(AzureClientCacheManager.getAzure(Mockito.eq(this.azureCloudUser)));
    }

}
