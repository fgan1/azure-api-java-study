package com.fgan.azure.api;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Creatable;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Completable;
import rx.Observable;

public class ManagerApi {

    public static ResourceGroup getResourceGroup(Azure azure, String name) {
        return azure.resourceGroups().getByName(name);
    }

    public static void removeResourceGroup(Azure azure, String name) {
        azure.resourceGroups().deleteByName(name);
    }

    public static void removeResourceGroupAsync(Azure azure, String name) {
        azure.resourceGroups().deleteByNameAsync(name)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .doOnCompleted(() -> System.out.println("Complete"))
                .subscribe();
    }

    public static ResourceGroup createResourceGroupName(Azure azure, String name) {
        return azure.resourceGroups().define(name)
                .withRegion(Region.US_EAST)
                .create();
    }

    public static Observable<Indexable> createResourceGroupNameAsync(Azure azure, String name) {
        return azure.resourceGroups().define(name)
                .withRegion(Region.US_EAST)
                .createAsync();
    }

}
