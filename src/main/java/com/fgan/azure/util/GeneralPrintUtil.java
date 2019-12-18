package com.fgan.azure.util;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.compute.*;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GeneralPrintUtil {

    public static void printNetworksLines(PagedList<Network> networks) {
        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                Network network = (Network) object;
                GeneralPrintUtil.printLines(network::name, network::id, network::regionName);
            }
        } , networks);
    }

    public static void printNetworkInterfacessLines(PagedList<NetworkInterface> networkInterfaces) {
        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                NetworkInterface networkInterface = (NetworkInterface) object;
                GeneralPrintUtil.printLines(networkInterface::name, networkInterface::id, networkInterface::key);
            }
        } , networkInterfaces);
    }

    public static void printVirtualMachineImageLines(PagedList<VirtualMachineImage> vmImages) {
        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineImage vmImage = (VirtualMachineImage) object;
                GeneralPrintUtil.printLines(vmImage::publisherName);
            }
        } , vmImages);
    }

    public static void printVirtualMachinesLines(PagedList<VirtualMachine> virtualMachine) {
        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                VirtualMachine vm = (VirtualMachine) object;
                GeneralPrintUtil.printLines(vm::name, vm::id, vm::size, vm::osDiskId);
            }
        }, virtualMachine);
    }

    public static void printVirtualMachineSizeLines(PagedList<VirtualMachineSize> virtualMachineSizes) {
        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                VirtualMachineSize vmSizes = (VirtualMachineSize) object;
                GeneralPrintUtil.printLines(vmSizes::name, vmSizes::numberOfCores, vmSizes::memoryInMB);
            }
        }, virtualMachineSizes);
    }

    public static void printVirtualMachinePublishersLines(
            PagedList<VirtualMachinePublisher> virtualMachinePublishers) {

        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                VirtualMachinePublisher virtualMachinePublisher = (VirtualMachinePublisher) object;
                GeneralPrintUtil.printLines(virtualMachinePublisher::name);
            }
        }, virtualMachinePublishers);
    }

    public static void printDisksLines(PagedList<Disk> disks) {
        GeneralPrintUtil.printSet(objects -> {
            for (Object object: objects) {
                Disk disk = (Disk) object;
                GeneralPrintUtil.printLines(disk::name, disk::id);
            }
        }, disks);
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
}
