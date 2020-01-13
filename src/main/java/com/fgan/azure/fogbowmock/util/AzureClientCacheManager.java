package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.Messages;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.rest.LogLevel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AzureClientCacheManager {
    private static final Logger LOGGER = Logger.getLogger(AzureClientCacheManager.class);

    private static final long LIFE_TIME_IN_MINUTES = 30;

    private final static LoadingCache<AzureCloudUser, Azure> loadingCache;

    static {
        loadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(LIFE_TIME_IN_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<AzureCloudUser, Azure>() {
                    @Override
                    public Azure load(AzureCloudUser azureCloudUser) throws Exception {
                        LOGGER.debug(Messages.CREATE_NEW_AZURE_CLIENTE);
                        return createAzure(azureCloudUser);
                    }
                });
    }

    public static Azure getAzure(AzureCloudUser azureCloudUser) throws UnauthenticatedUserException {
        try {
            return loadingCache.get(azureCloudUser);
        } catch (Exception e) {
            throw new UnauthenticatedUserException(e.getMessage(), e);
        }
    }

    @VisibleForTesting
    static Azure createAzure(AzureCloudUser azureCloudUser) throws FogbowException {
        try {
            AzureTokenCredentials azureTokenCredentials = buildAzureTokenCredentials(azureCloudUser);
            return Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(azureTokenCredentials)
                    .withDefaultSubscription();
        } catch (IOException | Error e) {
            throw new FogbowException("It was not possible create the Azure Client", e);
        }
    }

    @VisibleForTesting
    static AzureTokenCredentials buildAzureTokenCredentials(AzureCloudUser azureCloudUser) {
        String clientId = azureCloudUser.getClientId();
        String tenantId = azureCloudUser.getTenantId();
        String clientKey = azureCloudUser.getClientKey();
        String subscriptionId = azureCloudUser.getSubscriptionId();

        final String managementEndpoint = AzureEnvironment.AZURE.managementEndpoint();
        final String activeDirectoryEndpoint = AzureEnvironment.AZURE.activeDirectoryEndpoint();
        final String resourceManagerEndpoint = AzureEnvironment.AZURE.resourceManagerEndpoint();
        final String graphEndpoint = AzureEnvironment.AZURE.graphEndpoint();
        final String keyVaultDnsSuffix = AzureEnvironment.AZURE.keyVaultDnsSuffix();
        AzureEnvironment azureEnvironment = getAzureEnvironment(managementEndpoint, activeDirectoryEndpoint, resourceManagerEndpoint, graphEndpoint, keyVaultDnsSuffix);
        return getApplicationTokenCredentials(clientId, tenantId, clientKey, azureEnvironment)
                .withDefaultSubscriptionId(subscriptionId);
    }

    @VisibleForTesting
    static AzureEnvironment getAzureEnvironment(String managementEndpoint, String activeDirectoryEndpoint, String resourceManagerEndpoint, String graphEndpoint, String keyVaultDnsSuffix) {
        return new AzureEnvironment(new HashMap<String, String>() {
                {
                    this.put(AzureEnvironment.Endpoint.ACTIVE_DIRECTORY.toString(), activeDirectoryEndpoint.endsWith("/") ? activeDirectoryEndpoint : activeDirectoryEndpoint + "/");
                    this.put(AzureEnvironment.Endpoint.MANAGEMENT.toString(), managementEndpoint);
                    this.put(AzureEnvironment.Endpoint.RESOURCE_MANAGER.toString(), resourceManagerEndpoint);
                    this.put(AzureEnvironment.Endpoint.GRAPH.toString(), graphEndpoint);
                    this.put(AzureEnvironment.Endpoint.KEYVAULT.toString(), keyVaultDnsSuffix);
                }
            });
    }

    @VisibleForTesting
    static ApplicationTokenCredentials getApplicationTokenCredentials(String clientId, String tenantId, String clientKey, AzureEnvironment azureEnvironment) {
        return new ApplicationTokenCredentials(clientId, tenantId, clientKey, azureEnvironment);
    }


}
