package com.fgan.azure;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.compute.VirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachineSize;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PrintHolder {

    public static void printVirtualMachineImageLines(PagedList<VirtualMachineImage> vmImages) {
        PrintHolder.printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineImage vmImage = (VirtualMachineImage) object;
                PrintHolder.printLines(vmImage::publisherName);
            }
        } , vmImages);
    }

    public static void printVirtualMachineSizeLines(PagedList<VirtualMachineSize> virtualMachineSizes) {
        PrintHolder.printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineSize vmSizes = (VirtualMachineSize) object;
                PrintHolder.printLines(vmSizes::name, vmSizes::numberOfCores, vmSizes::memoryInMB);
            }
        }, virtualMachineSizes);
    }

    private static void printSet(Consumer<PagedList<? extends Object>> consumer,
                                PagedList<? extends Object> list) {
        System.out.println("-------------------------");
        consumer.accept(list);
        System.out.println("-------------------------");
    }

    public static void printLines(Supplier<? extends Object>... suppliers) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|");
        for (int i = 0; i < suppliers.length; i++) {
            stringBuilder.append(suppliers[i].get());
            stringBuilder.append("|");
        }
        System.out.println(stringBuilder.toString());
    }
}
