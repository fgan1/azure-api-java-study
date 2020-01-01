package com.fgan.azure.api.identity;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.AzureEnvironment.Endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class ApplicationTokenCredentials  {

    public static com.microsoft.azure.credentials.ApplicationTokenCredentials fromFile(File credentialsFile) throws IOException {
        Properties authSettings = new Properties();
        authSettings.put(ApplicationTokenCredentials.CredentialSettings.AUTH_URL.toString(), AzureEnvironment.AZURE.activeDirectoryEndpoint());
        authSettings.put(ApplicationTokenCredentials.CredentialSettings.BASE_URL.toString(), AzureEnvironment.AZURE.resourceManagerEndpoint());
        authSettings.put(ApplicationTokenCredentials.CredentialSettings.MANAGEMENT_URI.toString(), AzureEnvironment.AZURE.managementEndpoint());
        authSettings.put(ApplicationTokenCredentials.CredentialSettings.GRAPH_URL.toString(), AzureEnvironment.AZURE.graphEndpoint());
        authSettings.put(ApplicationTokenCredentials.CredentialSettings.VAULT_SUFFIX.toString(), AzureEnvironment.AZURE.keyVaultDnsSuffix());
        FileInputStream credentialsFileStream = new FileInputStream(credentialsFile);
        authSettings.load(credentialsFileStream);
        credentialsFileStream.close();
        String clientId = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.CLIENT_ID.toString());
        String tenantId = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.TENANT_ID.toString());
        String clientKey = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.CLIENT_KEY.toString());
        String certificate = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.CLIENT_CERT.toString());
        String certPasswrod = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.CLIENT_CERT_PASS.toString());
        final String mgmtUri = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.MANAGEMENT_URI.toString());
        final String authUrl = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.AUTH_URL.toString());
        final String baseUrl = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.BASE_URL.toString());
        final String graphUrl = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.GRAPH_URL.toString());
        final String vaultSuffix = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.VAULT_SUFFIX.toString());
        String defaultSubscriptionId = authSettings.getProperty(ApplicationTokenCredentials.CredentialSettings.SUBSCRIPTION_ID.toString());
        if (clientKey != null) {
            return (com.microsoft.azure.credentials.ApplicationTokenCredentials)(new com.microsoft.azure.credentials.ApplicationTokenCredentials(clientId, tenantId, clientKey, new AzureEnvironment(new HashMap<String, String>() {
                {
                    this.put(Endpoint.ACTIVE_DIRECTORY.toString(), authUrl.endsWith("/") ? authUrl : authUrl + "/");
                    this.put(Endpoint.MANAGEMENT.toString(), mgmtUri);
                    this.put(Endpoint.RESOURCE_MANAGER.toString(), baseUrl);
                    this.put(Endpoint.GRAPH.toString(), graphUrl);
                    this.put(Endpoint.KEYVAULT.toString(), vaultSuffix);
                }
            }))).withDefaultSubscriptionId(defaultSubscriptionId);
        } else if (certificate != null) {
            byte[] certs;
            if ((new File(certificate)).exists()) {
                certs = Files.readAllBytes(Paths.get(certificate));
            } else {
                certs = Files.readAllBytes(Paths.get(credentialsFile.getParent(), certificate));
            }

            return (com.microsoft.azure.credentials.ApplicationTokenCredentials)(new com.microsoft.azure.credentials.ApplicationTokenCredentials(clientId, tenantId, certs, certPasswrod, new AzureEnvironment(new HashMap<String, String>() {
                {
                    this.put(Endpoint.ACTIVE_DIRECTORY.toString(), authUrl);
                    this.put(Endpoint.MANAGEMENT.toString(), mgmtUri);
                    this.put(Endpoint.RESOURCE_MANAGER.toString(), baseUrl);
                    this.put(Endpoint.GRAPH.toString(), graphUrl);
                    this.put(Endpoint.KEYVAULT.toString(), vaultSuffix);
                }
            }))).withDefaultSubscriptionId(defaultSubscriptionId);
        } else {
            throw new IllegalArgumentException("Please specify either a client key or a client certificate.");
        }
    }

    private static enum CredentialSettings {
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

