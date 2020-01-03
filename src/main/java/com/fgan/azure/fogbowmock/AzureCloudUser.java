package com.fgan.azure.fogbowmock;

import cloud.fogbow.common.models.CloudUser;

public class AzureCloudUser extends CloudUser {

    private String clientId;
    private String tenantId;
    private String clientKey;
    private String subscriptionId;
    private String resourceGroupName;

    public AzureCloudUser(String userId, String userName, String clientId, String tenantId, String clientKey, String subscriptionId, String resourceGroupName) {
        super(userId, userName, "");
        this.clientId = clientId;
        this.tenantId = tenantId;
        this.clientKey = clientKey;
        this.subscriptionId = subscriptionId;
        this.resourceGroupName = resourceGroupName;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getResourceGroupName() {
        return resourceGroupName;
    }
}
