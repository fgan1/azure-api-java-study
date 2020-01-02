package com.fgan.azure.util;

import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class PropertiesUtil {

    public static final String GENERAL_PROPERTIES_ENV = "GENERAL_PROPERTIES_ENV";
    public final static String AZURE_AUTH_LOCATION_PROPERTIES_ENV = "AZURE_AUTH_LOCATION";
    public static final String COMPUTE_PLUGIN_PROPERTIES_ENV = "COMPUTE_PLUGIN_PROPERTIES_ENV";

    private static final String SUBSCRIPTION_PROPS = "subscription";
    private static final String NETWORK_INTEFACE_ID_PROPS = "network_interface_id";
    private static final String RESOURCE_GROUP_NAME_PROPS = "resource_group_name";
    private static final String CLOUD_INIT_PATH_PROPS = "cloud_init_path";
    private static final String USER_NAME_PROPS = "user_name";
    private static final String USER_PASSWORD_PROPS = "user_password";
    private static final String CLIENT_ID_PROPS = "client";
    private static final String TENTANT_ID_PROPS = "tenant";
    private static final String GRAPH_URL_PROPS = "graphURL";

    private static Properties properties;

    public static Properties getInstance() {
        if (properties != null) {
            return properties;
        }
        return getProperties();
    }

    public static String getUserPasswordProps() {
        return getProps(USER_PASSWORD_PROPS);
    }

    public static String getUserNameProps() {
        return getProps(USER_NAME_PROPS);
    }

    public static String getSubscriptionProp() {
        return getProps(SUBSCRIPTION_PROPS);
    }

    public static String getNetworkInterfaceIdProp() {
        return getProps(NETWORK_INTEFACE_ID_PROPS);
    }

    public static String getClientIdProps() {
        return getProps(CLIENT_ID_PROPS);
    }

    /**
     * Note: Graph Url is known as Resouce in some cases
     */
    public static String getGraphURLProps() {
        return getProps(GRAPH_URL_PROPS);
    }

    public static String getTentantIdProps() {
        return getProps(TENTANT_ID_PROPS);
    }

    public static String getResourceGroupNameProp() {
        return getProps(RESOURCE_GROUP_NAME_PROPS);
    }

    private static String getProps(String type) {
        Properties properties = getInstance();
        String value = properties.getProperty(type);
        if (properties == null) {
            String errorMsg = String.format("The propertie %s is null", type);
            throw new Error(errorMsg);
        }
        System.out.println(String.format("Propertie %s / Value: %s", type, value));
        return value;
    }

    public static String getUserData() {
        Properties properties = getInstance();
        String cloudInitPath = properties.getProperty(CLOUD_INIT_PATH_PROPS);
        String cloudInitContent = getFileContent(cloudInitPath);
        return new String(Base64.encodeBase64(cloudInitContent.getBytes(StandardCharsets.UTF_8),
                false, false), StandardCharsets.UTF_8);
    }

    private static Properties getProperties() {
        String generalPropertiesPath = System.getenv(GENERAL_PROPERTIES_ENV);
        Properties generalProperties = loadProperties(generalPropertiesPath);

        String azuteAuthPropertiesPath = System.getenv(AZURE_AUTH_LOCATION_PROPERTIES_ENV);
        Properties azureAuthProperties = loadProperties(azuteAuthPropertiesPath);

        Properties properties = new Properties();
        properties.putAll(generalProperties);
        properties.putAll(azureAuthProperties);
        return properties;
    }

    private static Properties loadProperties(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        } catch (IOException e) {
            String errorMsg = String.format("There is no possible lead the properties by the path: %s ", path);
            throw new Error(errorMsg, e);
        }
    }

    public static String getAzureAuthLocationPath() {
        return System.getenv(AZURE_AUTH_LOCATION_PROPERTIES_ENV);
    }

    private static String getFileContent(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(line -> contentBuilder
                    .append(line)
                    .append(System.getProperty("line.separator")));
        } catch (IOException e) {
            throw new Error("There is no possible get the file content", e);
        }
        return contentBuilder.toString();
    }

}
