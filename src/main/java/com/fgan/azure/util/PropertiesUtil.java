package com.fgan.azure.util;

import com.sun.istack.internal.Nullable;
import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class PropertiesUtil {

    private static final String GENERAL_PROPERTIES_ENV = "GENERAL_PROPERTIES_ENV";

    private static final String SUBSCRIPTION_PROPS = "subscription";
    private static final String NETWORK_INTEFACE_ID_PROPS = "network_interface_id";
    private static final String RESOURCE_GROUP_NAME_PROPS = "resource_group_name";
    private static final String CLOUD_INIT_PATH_PROPS = "cloud_init_path";

    private static Properties properties;

    private static Properties getInstance() {
        if (properties != null) {
            return properties;
        }
        return getProperties();
    }

    @Nullable
    public static String getSubscriptionProp() {
        Properties properties = getInstance();
        return properties.getProperty(SUBSCRIPTION_PROPS);
    }

    @Nullable
    public static String getNetworkInterfaceIdProp() {
        Properties properties = getInstance();
        return properties.getProperty(NETWORK_INTEFACE_ID_PROPS);
    }

    @Nullable
    public static String getResourceGroupNameProp() {
        Properties properties = getInstance();
        return properties.getProperty(RESOURCE_GROUP_NAME_PROPS);
    }

    public static String getUserData() {
        Properties properties = getInstance();
        String cloudInitPath = properties.getProperty(CLOUD_INIT_PATH_PROPS);
        String cloudInitContent = getFileContent(cloudInitPath);
        return new String(Base64.encodeBase64(cloudInitContent.getBytes(StandardCharsets.UTF_8),
                false, false), StandardCharsets.UTF_8);
    }

    private static Properties getProperties() {
        try {
            String generalPropertiesPath = System.getenv(GENERAL_PROPERTIES_ENV);
            generalPropertiesPath = "/home/chico/git/azure-api-java-study/src/main/resources/general.properties"; // TODO(fgan): remove this
            FileInputStream fileInputStream = new FileInputStream(generalPropertiesPath);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        } catch (IOException e) {
            throw new Error("There is no possible ** the properties", e);
        }
    }

    private static String getFileContent(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw new Error("There is no possible get the file content", e);
        }
        return contentBuilder.toString();
    }

}
