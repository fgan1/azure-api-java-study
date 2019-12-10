package com.fgan.azure;

import com.sun.istack.internal.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHolder {

    private static final String GENERAL_PROPERTIES_ENV = "GENERAL_PROPERTIES_ENV";

    private static final String NETWORK_INTEFACE_ID_PROPS = "network_interface_id";
    private static final String RESOURCE_GROUP_NAME_PROPS = "resource_group_name";

    private static Properties properties;

    private static Properties getInstance() {
        if (properties != null) {
            return properties;
        }
        return getProperties();
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

}
