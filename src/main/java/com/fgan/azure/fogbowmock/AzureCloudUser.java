package com.fgan.azure.fogbowmock;

import cloud.fogbow.common.models.CloudUser;

public class AzureCloudUser extends CloudUser {

    private String clientId;
    private String tenantId;
    private String clientKey;
    private String subscriptionId;

    public AzureCloudUser(String userId, String userName, String clientId, String tenantId, String clientKey, String subscriptionId) {
        super(userId, userName, "");
        this.clientId = clientId;
        this.tenantId = tenantId;
        this.clientKey = clientKey;
        this.subscriptionId = subscriptionId;
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
}
