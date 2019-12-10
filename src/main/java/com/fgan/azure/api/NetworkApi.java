package com.fgan.azure.api;

import com.fgan.azure.PrintHolder;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.sun.istack.internal.Nullable;

public class NetworkApi {

    public static void printInformation(Azure azure) {
        PagedList<Network> networks = getNetworks(azure);
        PrintHolder.printNetworksLines(networks);

        PagedList<NetworkInterface> networkInterfaces = getNetworkInterfaces(azure);
        PrintHolder.printNetworkInterfacessLines(networkInterfaces);
    }

    @Nullable
    public static NetworkInterface getNetworkInterface(Azure azure, String id) {
        return azure.networkInterfaces().getById(id);
    }

    @Nullable
    public static Network getNetwork(Azure azure, String id) {
        return azure.networks().getById(id);
    }

    private static PagedList<Network> getNetworks(Azure azure) {
        return azure.networks().list();
    }

    private static PagedList<NetworkInterface> getNetworkInterfaces(Azure azure) {
        return azure.networkInterfaces().list();
    }

}
