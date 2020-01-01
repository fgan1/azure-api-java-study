package com.fgan.azure.fogbowmock.compute;

import com.fgan.azure.fogbowmock.AzureVirtualMachineImage;

public class AzureVirtualMachineParameters {

    private String networkInterfaceId;
    private String resourceGroupName;
    private String virtualMachineName;
    private String regionName;
    private AzureVirtualMachineImage azureVirtualMachineImage;
    private String userData;
    private String size;
    private String osUserName;
    private String osUserPassword;
    private String osComputeName;

    public String getNetworkInterfaceId() {
        return networkInterfaceId;
    }

    public void setNetworkInterfaceId(String networkInterfaceId) {
        this.networkInterfaceId = networkInterfaceId;
    }

    public String getResourceGroupName() {
        return resourceGroupName;
    }

    public void setResourceGroupName(String resourceGroupName) {
        this.resourceGroupName = resourceGroupName;
    }

    public String getVirtualMachineName() {
        return virtualMachineName;
    }

    public void setVirtualMachineName(String virtualMachineName) {
        this.virtualMachineName = virtualMachineName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public AzureVirtualMachineImage getAzureVirtualMachineImage() {
        return azureVirtualMachineImage;
    }

    public void setAzureVirtualMachineImage(AzureVirtualMachineImage azureVirtualMachineImage) {
        this.azureVirtualMachineImage = azureVirtualMachineImage;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOsUserName() {
        return osUserName;
    }

    public void setOsUserName(String osUserName) {
        this.osUserName = osUserName;
    }

    public String getOsUserPassword() {
        return osUserPassword;
    }

    public void setOsUserPassword(String osUserPassword) {
        this.osUserPassword = osUserPassword;
    }

    public String getOsComputeName() {
        return osComputeName;
    }

    public void setOsComputeName(String osComputeName) {
        this.osComputeName = osComputeName;
    }
}
