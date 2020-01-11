package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.compute.VirtualMachineSizes;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import rx.Completable;
import rx.Observable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface AzureVirtualMachineSDK {

    // TODO(chico) - Implement tests
    static Observable<Indexable> buildVirtualMachineObservable(Azure azure, String virtualMachineName, Region region,
                                                               String resourceGroupName, NetworkInterface networkInterface,
                                                               String imagePublished, String imageOffer, String imageSku,
                                                               String osUserName, String osUserPassword, String osComputeName,
                                                               String userData, int diskSize, String size) {

        VirtualMachine.DefinitionStages.WithOS osChoosen = azure.virtualMachines()
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
//                .withOSDiskSizeInGB(diskSize)
                .withSize(size)
                .createAsync();
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

    static PagedList<VirtualMachineSize> getVirtualMachineSizes(Azure azure, Region region)
            throws AzureException.Unexpected {

        try {
            VirtualMachineSizes sizes = azure.virtualMachines().sizes();
            return sizes.listByRegion(region);
        } catch (RuntimeException e) {
            throw new AzureException.Unexpected(e);
        }
    }

    static Optional<VirtualMachine> getVirtualMachineById(Azure azure, String virtualMachineId)
            throws AzureException.Unexpected {

        try {
            return Optional.ofNullable(azure.virtualMachines().getById(virtualMachineId));
        } catch (RuntimeException e) {
            throw new AzureException.Unexpected(e);
        }
    }

    static Completable buildDeleteVirtualMachineCompletable(Azure azure, String virtualMachineId) {
        return azure.virtualMachines().deleteByIdAsync(virtualMachineId);
    }
}
