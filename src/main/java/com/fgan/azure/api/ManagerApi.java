package com.fgan.azure.api;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.ResourceGroup;

public class ManagerApi {

    public static ResourceGroup getResourceGroup(Azure azure, String name) {
        return azure.resourceGroups().getByName(name);
    }

}
