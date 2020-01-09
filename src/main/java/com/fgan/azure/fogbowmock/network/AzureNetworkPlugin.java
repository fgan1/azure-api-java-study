package com.fgan.azure.fogbowmock.network;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.NetworkInstance;
import cloud.fogbow.ras.core.models.orders.NetworkOrder;
import cloud.fogbow.ras.core.plugins.interoperability.NetworkPlugin;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.util.AzureConstants;

import java.util.Properties;

public class AzureNetworkPlugin implements NetworkPlugin<AzureCloudUser> {

    private final String resourceGroupName;

    public AzureNetworkPlugin(String confFilePath) {
        Properties properties = PropertiesUtil.readProperties(confFilePath);

        this.resourceGroupName = properties.getProperty(AzureConstants.DEFAULT_RESOURCE_GROUP_NAME_KEY);
    }

    @Override
    public boolean isReady(String s) {
        return false;
    }

    @Override
    public boolean hasFailed(String s) {
        return false;
    }

    @Override
    public String requestInstance(NetworkOrder networkOrder, AzureCloudUser azureCloudUser) throws FogbowException {


        return null;
    }

    @Override
    public NetworkInstance getInstance(NetworkOrder networkOrder, AzureCloudUser azureCloudUser) throws FogbowException {
        return null;
    }

    @Override
    public void deleteInstance(NetworkOrder networkOrder, AzureCloudUser azureCloudUser) throws FogbowException {

    }
}
