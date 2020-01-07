package com.fgan.azure;

import cloud.fogbow.common.util.HomeDir;
import cloud.fogbow.ras.constants.SystemConstants;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import org.mockito.Mockito;

import java.io.File;

public class AzureTestUtils {

    private static final String SUBSCRIPTION_ID_DEFAULT = "subscriptionId";
    private static String CLOUD_NAME = "azure";

    public static String AZURE_CONF_FILE_PATH = HomeDir.getPath()
            + SystemConstants.CLOUDS_CONFIGURATION_DIRECTORY_NAME + File.separator +
            CLOUD_NAME + File.separator + SystemConstants.CLOUD_SPECIFICITY_CONF_FILE_NAME;

    public static AzureCloudUser createAzureCloudUser() {
        AzureCloudUser azureCloudUser = Mockito.mock(AzureCloudUser.class);
        Mockito.when(azureCloudUser.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID_DEFAULT);
        return azureCloudUser;
    }
}
