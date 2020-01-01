package com.fgan.azure.util;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.compute.*;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class GeneralPrintUtil {

    public static void printNetworksLines(PagedList<Network> networks) {
        printSet(objects -> {
            for (Object object: objects) {
                Network network = (Network) object;
                GeneralPrintUtil.printLines(network::name, network::id, network::regionName);
            }
        } , networks);
    }

    public static void printNetworkInterfacessLines(PagedList<NetworkInterface> networkInterfaces) {
        printSet(objects -> {
            for (Object object: objects) {
                NetworkInterface networkInterface = (NetworkInterface) object;
                GeneralPrintUtil.printLines(networkInterface::name, networkInterface::id, networkInterface::key);
            }
        } , networkInterfaces);
    }

    public static void printVirtualMachineImageLines(PagedList<VirtualMachineImage> vmImages) {
        printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineImage vmImage = (VirtualMachineImage) object;
                GeneralPrintUtil.printLines(vmImage::publisherName);
            }
        } , vmImages);
    }

    public static void printVirtualMachinesLines(PagedList<VirtualMachine> virtualMachine) {
        printSet(objects -> {
            for (Object object: objects) {
                VirtualMachine vm = (VirtualMachine) object;
                GeneralPrintUtil.printLines(vm::name, vm::id, vm::size, vm::osDiskId);
            }
        }, virtualMachine);
    }

    public static void printVirtualMachineSizeLines(PagedList<VirtualMachineSize> virtualMachineSizes) {
        printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineSize vmSizes = (VirtualMachineSize) object;
                GeneralPrintUtil.printLines(vmSizes::name, vmSizes::numberOfCores, vmSizes::memoryInMB);
            }
        }, virtualMachineSizes);
    }

    public static void printVirtualMachinePublishersLines(
            PagedList<VirtualMachinePublisher> virtualMachinePublishers) {

        printSet(objects -> {
            for (Object object: objects) {
                VirtualMachinePublisher virtualMachinePublisher = (VirtualMachinePublisher) object;
                PagedList<VirtualMachineOffer> virtualMachineOffers = virtualMachinePublisher.offers().list();
                for (VirtualMachineOffer virtualMachineOffer : virtualMachineOffers) {
                    PagedList<VirtualMachineSku> virtualMachineSkus = virtualMachineOffer.skus().list();
                    for (VirtualMachineSku machineSkus : virtualMachineSkus) {
                        putLevelPrint(2);
                        GeneralPrintUtil.printLines(virtualMachineOffer::name);
                    }
                    putLevelPrint(1);
                    GeneralPrintUtil.printLines(virtualMachineOffer::name);
                }
                GeneralPrintUtil.printLines(virtualMachinePublisher::name);
            }
        }, virtualMachinePublishers);
    }

    public static void printDisksLines(PagedList<Disk> disks) {
        printSet(objects -> {
            for (Object object: objects) {
                Disk disk = (Disk) object;
                GeneralPrintUtil.printLines(disk::name, disk::id);
            }
        }, disks);
    }

    public static void printVirtualMachineImage(PagedList<VirtualMachineImage> virtualMachineImages) {
        printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineImage virtualMachineImage = (VirtualMachineImage) object;
                GeneralPrintUtil.printLines(virtualMachineImage::publisherName,
                        virtualMachineImage::offer,
                        virtualMachineImage::sku,
                        virtualMachineImage::id);
            }
        }, virtualMachineImages);

    }

    public static void printComputeUsages(PagedList<ComputeUsage> computeUsages) {
        printSet(objects -> {
            for (Object object: objects) {
                ComputeUsage computeUsage = (ComputeUsage) object;
                UsageName usageName = computeUsage.name();
                GeneralPrintUtil.printLines(usageName::value,
                        usageName::localizedValue,
                        computeUsage::currentValue,
                        computeUsage::limit,
                        computeUsage::unit);
            }
        }, computeUsages);
    }

    private static void printSet(Consumer<PagedList<? extends Object>> functionConsumer,
                                PagedList<? extends Object> list) {

        System.out.println("--------------------------");
        functionConsumer.accept(list);
        System.out.println("--------------------------");
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

    public static void putLevelPrint(int level) {
        IntStream.range(0, level).forEach(value -> {
            System.out.print("--");
        });
    }
}
