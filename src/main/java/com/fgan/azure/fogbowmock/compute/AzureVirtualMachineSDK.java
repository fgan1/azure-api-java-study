package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.UnexpectedException;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.compute.VirtualMachineSizes;
import com.microsoft.azure.management.compute.VirtualMachines;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Completable;
import rx.Observable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface AzureVirtualMachineSDK {

    static Observable<Indexable> buildVirtualMachineObservable(Azure azure, String virtualMachineName, Region region,
                                                               String resourceGroupName, NetworkInterface networkInterface,
                                                               String imagePublished, String imageOffer, String imageSku,
                                                               String osUserName, String osUserPassword, String osComputeName,
                                                               String userData, int diskSize, String size) {

        VirtualMachines virtualMachine = getVirtualMachinesObject(azure);

        VirtualMachine.DefinitionStages.WithOS osChoosen = virtualMachine
                .define(virtualMachineName)
                .withRegion(region)
                .withExistingResourceGroup(resourceGroupName)
                .withExistingPrimaryNetworkInterface(networkInterface);

        VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged optionsManaged;
        if (isWindowsImage(imageOffer, imageSku)) {
            optionsManaged = osChoosen.withLatestWindowsImage(imagePublished, imageOffer, imageSku)
                    .withAdminUsername(osUserName)
                    .withAdminPassword(osUserPassword)
                    .withComputerName(osComputeName);
        } else {
            optionsManaged = osChoosen.withLatestLinuxImage(imagePublished, imageOffer, imageSku)
                    .withRootUsername(osUserName)
                    .withRootPassword(osUserPassword)
                    .withComputerName(osComputeName);
        }
        return optionsManaged
                .withCustomData(userData)
                .withOSDiskSizeInGB(diskSize)
                .withSize(size)
                .createAsync();
    }

    static PagedList<VirtualMachineSize> getVirtualMachineSizes(Azure azure, Region region)
            throws UnexpectedException {

        try {
            VirtualMachines virtualMachinesObject = getVirtualMachinesObject(azure);
            VirtualMachineSizes sizes = virtualMachinesObject.sizes();
            return sizes.listByRegion(region);
        } catch (RuntimeException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
    }

    static Optional<VirtualMachine> getVirtualMachineById(Azure azure, String virtualMachineId)
            throws UnexpectedException {

        try {
            VirtualMachines virtualMachinesObject = getVirtualMachinesObject(azure);
            return Optional.ofNullable(virtualMachinesObject.getById(virtualMachineId));
        } catch (RuntimeException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
    }

    static Completable buildDeleteVirtualMachineCompletable(Azure azure, String virtualMachineId) {
        return azure.virtualMachines().deleteByIdAsync(virtualMachineId);
    }

    @VisibleForTesting
    static boolean isWindowsImage(String imageOffer, String imageSku) {
        return constainsWindownsOn(imageOffer) || constainsWindownsOn(imageSku);
    }

    @VisibleForTesting
    static boolean constainsWindownsOn(String text) {
        String regex = ".*windows.*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcherOffer = pattern.matcher(text);
        return matcherOffer.find();
    }

    // This class is used only for test proposes. It is necessary because was not possible mock the Azure(final class)
    @VisibleForTesting
    static VirtualMachines getVirtualMachinesObject(Azure azure) {
        return azure.virtualMachines();
    }
}
