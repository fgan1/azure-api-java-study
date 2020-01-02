package com.fgan.azure.fogbowmock;

import cloud.fogbow.common.exceptions.FogbowException;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.rest.LogLevel;

import java.io.IOException;
import java.util.HashMap;

public class AzureClientUtil {

    public static Azure getAzure(AzureCloudUser azureCloudUser) throws FogbowException {
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
        AzureTokenCredentials azureTokenCredentials =
                new ApplicationTokenCredentials(clientId, tenantId, clientKey, azureEnvironment)
                .withDefaultSubscriptionId(defaultSubscriptionId);

        try {
            return Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(azureTokenCredentials)
                    .withDefaultSubscription();
        } catch (IOException e) {
            throw new FogbowException("It was not possible create the Azure Client", e);
        }
    }

    public enum CredentialSettings {
        SUBSCRIPTION_ID("subscription"),
        TENANT_ID("tenant"),
        CLIENT_ID("client"),
        CLIENT_KEY("key"),
        CLIENT_CERT("certificate"),
        CLIENT_CERT_PASS("certificatePassword"),
        MANAGEMENT_URI("managementURI"),
        BASE_URL("baseURL"),
        AUTH_URL("authURL"),
        GRAPH_URL("graphURL"),
        VAULT_SUFFIX("vaultSuffix");

        private final String name;

        private CredentialSettings(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

}
