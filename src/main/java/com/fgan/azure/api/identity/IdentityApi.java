package com.fgan.azure.api.identity;

import com.fgan.azure.Constants;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.util.PropertiesUtil;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.UserTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.Tenants;
import com.microsoft.rest.LogLevel;

import javax.naming.ServiceUnavailableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Samples Identity operation:
 * - https://github.com/AzureAD/azure-activedirectory-library-for-java/blob/dev/src/samples/public-client-app-sample/src/main/java/PublicClient.java
 * - https://github.com/AzureAD/azure-activedirectory-library-for-java
 * - https://github.com/Azure-Samples/active-directory-java-native-headless-v2
 * - https://github.com/Azure/ms-rest-nodeauth
 * <p>
 * Pre requirementes note:
 * - Grant the applition generate token
 * - Grant permission in the application
 */
public class IdentityApi {

    public static Azure getAzure() throws Exception {
        System.out.println("Getting azure object");

        final File azureCredentialsFile = getAzureCredentialsFile();
        try {
            return Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(azureCredentialsFile)
                    .withDefaultSubscription();
        } catch (IOException e) {
            throw new Exception("Is not possible get Azure object", e);
        }
    }

    private static File getAzureCredentialsFile() throws Exception {
        String azureAuthLocation = PropertiesUtil.getAzureAuthLocationPath();
        final File credFile = new File(azureAuthLocation);
        if (!credFile.exists()) {
            String errorMsg = String.format("There is no file on path: %s", azureAuthLocation);
            throw new Exception(errorMsg);
        }
        return credFile;
    }

    private static Properties getAzureCredentialsProperties() throws Exception {
        Properties credentials = new Properties();
        File azureCredentialsFile = getAzureCredentialsFile();
        FileInputStream credentialsFileStream = new FileInputStream(azureCredentialsFile);
        credentials.load(credentialsFileStream);
        credentialsFileStream.close();
        return credentials;
    }

    public static AzureCloudUser getAzureCloudUser() throws Exception {
        Properties credentials = getAzureCredentialsProperties();

        String clientId = credentials.getProperty(CredentialSettings.CLIENT_ID.toString());
        String tenantId = credentials.getProperty(CredentialSettings.TENANT_ID.toString());
        String clientKey = credentials.getProperty(CredentialSettings.CLIENT_KEY.toString());
        String subscriptionId = credentials.getProperty(CredentialSettings.SUBSCRIPTION_ID.toString());;
        String resourceGroupName =  credentials.getProperty(CredentialSettings.RESOURCE_GROUP_NAME.toString());
        String regionName =  credentials.getProperty(CredentialSettings.REGION_NAME.toString());
        return new AzureCloudUser("", "", clientId, tenantId,
                clientKey, subscriptionId, resourceGroupName, regionName);
    }

    /**
     * Note: Using Fogbow envirement to test it.
     */
    public static Azure getAzureFogbow() throws Exception {
        AzureCloudUser azureCloudUser = getAzureCloudUser();
        return AzureClientCacheManager.getAzure(azureCloudUser);
    }

    @Deprecated
    /**
     * Check authentication by making a request. It is not a good way.
     */
    public static void checkAuthenticationByRequest() {
        String clientId = PropertiesUtil.getClientIdProps();
        String tenantId = PropertiesUtil.getTentantIdProps();
        String password = PropertiesUtil.getUserPasswordProps();
        String username = PropertiesUtil.getUserNameProps();
        Azure.Authenticated authenticate = authenticate(clientId, tenantId, username, password);
        Tenants tenants = authenticate.tenants();
        try {
            tenants.list();
            System.out.println("authenticated");
        } catch (RuntimeException e) {
            // TODO(chico) - treat batter this exception
            System.out.println("not authenticated");
        }
    }

    public static void checkAuthentication() throws ServiceUnavailableException {
        String password = PropertiesUtil.getUserPasswordProps();
        String username = PropertiesUtil.getUserNameProps();

        AuthenticationResult result = createUserToken(username, password);
        System.out.println("Access Token - " + result.getAccessToken());
        System.out.println("Refresh Token - " + result.getRefreshToken());
        System.out.println("ID Token - " + result.getIdToken());
    }

    /**
     * @param clientId The active directory application client id.
     * @param tenantId The domain or tenant id containing this application.
     * @param username The user name for the Organization Id account
     * @param password The password for the Organization Id account
     */
    private static Azure.Authenticated authenticate(String clientId, String tenantId, String username, String password) {
        UserTokenCredentials credentials = new UserTokenCredentials(
                clientId,
                tenantId,
                username,
                password,
                AzureEnvironment.AZURE);
        return Azure.authenticate(credentials);
    }

    /**
     *
     */
    private static AuthenticationResult createUserToken(
            String username, String password) throws ServiceUnavailableException {

        String resource = PropertiesUtil.getGraphURLProps();
        String clientId = PropertiesUtil.getClientIdProps();
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            AuthenticationContext context = new AuthenticationContext(
                    Constants.AUTHORITY_PATH, false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                    resource, clientId, username, password, null);
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }

        throw new ServiceUnavailableException("Authentication result was null");
    }

    public enum CredentialSettings {
        SUBSCRIPTION_ID("subscription"),
        RESOURCE_GROUP_NAME("resourceGroupName"),
        REGION_NAME("regionName"),
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
