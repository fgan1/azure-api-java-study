package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.FogbowException;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

// TODO(chico) - implement tests
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
                        LOGGER.debug("Creating a new Azure client");
                        return getAzureProperly(azureCloudUser);
                    }
                });
    }

    public static Azure getAzure(AzureCloudUser azureCloudUser) throws AzureException.Unauthenticated {
        try {
            LOGGER.debug("Trying to get Azure client in the cache");
            return loadingCache.get(azureCloudUser);
        } catch (ExecutionException e) {
            throw new AzureException.Unauthenticated(e);
        }
    }

    public static Azure getAzureProperly(AzureCloudUser azureCloudUser) throws FogbowException {
        AzureTokenCredentials azureTokenCredentials = getAzureTokenCredentials(azureCloudUser);

        try {
            return Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(azureTokenCredentials)
                    .withDefaultSubscription();
        } catch (IOException e) {
            throw new FogbowException("It was not possible create the Azure Client", e);
        }
    }

    private static AzureTokenCredentials getAzureTokenCredentials(AzureCloudUser azureCloudUser) {
        String clientId = azureCloudUser.getClientId();
        String tenantId = azureCloudUser.getTenantId();
        String clientKey = azureCloudUser.getClientKey();
        String defaultSubscriptionId = azureCloudUser.getSubscriptionId();

        final String mgmtUri = AzureEnvironment.AZURE.managementEndpoint();
        final String authUrl = AzureEnvironment.AZURE.activeDirectoryEndpoint();
        final String baseUrl = AzureEnvironment.AZURE.resourceManagerEndpoint();
        final String graphUrl = AzureEnvironment.AZURE.graphEndpoint();
        final String vaultSuffix = AzureEnvironment.AZURE.keyVaultDnsSuffix();
        AzureEnvironment azureEnvironment = new AzureEnvironment(new HashMap<String, String>() {
            {
                this.put(AzureEnvironment.Endpoint.ACTIVE_DIRECTORY.toString(), authUrl.endsWith("/") ? authUrl : authUrl + "/");
                this.put(AzureEnvironment.Endpoint.MANAGEMENT.toString(), mgmtUri);
                this.put(AzureEnvironment.Endpoint.RESOURCE_MANAGER.toString(), baseUrl);
                this.put(AzureEnvironment.Endpoint.GRAPH.toString(), graphUrl);
                this.put(AzureEnvironment.Endpoint.KEYVAULT.toString(), vaultSuffix);
            }
        });
        return new ApplicationTokenCredentials(clientId, tenantId, clientKey, azureEnvironment)
                .withDefaultSubscriptionId(defaultSubscriptionId);
    }

    public enum CredentialSettings {
        SUBSCRIPTION_ID("subscription"),
        RESOURCE_GROUP_NAME("resourceGroupName"),
        TENANT_ID("tenant"),
        CLIENT_ID("client"),
        CLIENT_KEY("key"),
        MANAGEMENT_URI("managementURI"),
        BASE_URL("baseURL"),
        AUTH_URL("authURL"),
        GRAPH_URL("graphURL");

        private final String name;

        private CredentialSettings(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

}
