package com.fgan.azure.api;

import com.fgan.azure.Constants;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.util.PropertiesUtil;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.PublicIPAddress;

/**
 * Samples Public Ip operation:
 * - https://github.com/Azure-Samples/network-java-manage-ip-address
 *
 * Fogbow Context:
 * PublicIp is a PublicIpAddress
 */
public class PublicIpApi {

    public static final String PUBLIC_IP_NAME_DEFAULT = Constants.PREFIX + "publicIp";

    public static void attachPublicIdToVm(Azure azure, String virtualMachineId, String publicIpAddressId)
            throws AzureException.ResourceNotFound {

        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);
        PublicIPAddress publicIpAddress = getPublicIpAddress(azure, publicIpAddressId);
        NetworkInterface primaryNetworkInterface = virtualMachine.getPrimaryNetworkInterface();
        primaryNetworkInterface.update()
                .withExistingPrimaryPublicIPAddress(publicIpAddress)
                .apply();
    }

    public static void detachPublicIdToVm(Azure azure, String virtualMachineId)
            throws AzureException.ResourceNotFound {

        VirtualMachine virtualMachine = ComputeApi.getVirtualMachineById(azure, virtualMachineId);
        NetworkInterface primaryNetworkInterface = virtualMachine.getPrimaryNetworkInterface();
        primaryNetworkInterface.update()
                .withoutPrimaryPublicIPAddress()
                .apply();
    }

    public static PublicIPAddress getPublicIpAddress(Azure azure, String publicIpAddessId) {
        return azure.publicIPAddresses().getById(publicIpAddessId);
    }

    public static PublicIPAddress createPublicIpAddress(Azure azure) {
        String resourceGroupName = PropertiesUtil.getResourceGroupNameProp();
        return createPublicIpAddress(azure, resourceGroupName);
    }

    public static PublicIPAddress createPublicIpAddress(Azure azure, String resourceGroupName) {
        return azure.publicIPAddresses().define(PUBLIC_IP_NAME_DEFAULT)
                .withRegion(Constants.REGION_DEFAULT)
                .withNewResourceGroup(resourceGroupName)
                .withLeafDomainLabel(PUBLIC_IP_NAME_DEFAULT)
                .create();
    }

}
