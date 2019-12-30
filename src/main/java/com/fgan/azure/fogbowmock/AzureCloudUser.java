package com.fgan.azure.fogbowmock;

import cloud.fogbow.common.models.CloudUser;

public class AzureCloudUser extends CloudUser {
    public AzureCloudUser(String userId, String userName, String token) {
        super(userId, userName, token);
    }
}
