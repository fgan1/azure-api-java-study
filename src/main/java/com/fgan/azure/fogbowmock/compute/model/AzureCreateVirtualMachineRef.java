package com.fgan.azure.fogbowmock.compute.model;

import com.fgan.azure.fogbowmock.util.GenericBuilder;

import java.util.function.Supplier;

public class AzureCreateVirtualMachineRef {

    private AzureGetImageRef azureVirtualMachineImage;
    private String networkInterfaceId;
    private String resourceGroupName;
    private String virtualMachineName;
    private String osUserPassword;
    private String osComputeName;
    private String osUserName;
    private String regionName;
    private String userData;
    private int diskSize;
    private String size;

    public static Builder builder() {
        return new Builder(AzureCreateVirtualMachineRef::new);
    }

    public AzureGetImageRef getAzureVirtualMachineImage() {
        return azureVirtualMachineImage;
    }

    private void setAzureVirtualMachineImage(AzureGetImageRef azureVirtualMachineImage) {
        this.azureVirtualMachineImage = azureVirtualMachineImage;
    }

    public String getNetworkInterfaceId() {
        return networkInterfaceId;
    }

    private void setNetworkInterfaceId(String networkInterfaceId) {
        this.networkInterfaceId = networkInterfaceId;
    }

    public String getResourceGroupName() {
        return resourceGroupName;
    }

    private void setResourceGroupName(String resourceGroupName) {
        this.resourceGroupName = resourceGroupName;
    }

    public String getVirtualMachineName() {
        return virtualMachineName;
    }

    private void setVirtualMachineName(String virtualMachineName) {
        this.virtualMachineName = virtualMachineName;
    }

    public String getOsUserPassword() {
        return osUserPassword;
    }

    private void setOsUserPassword(String osUserPassword) {
        this.osUserPassword = osUserPassword;
    }

    public String getOsComputeName() {
        return osComputeName;
    }

    private void setOsComputeName(String osComputeName) {
        this.osComputeName = osComputeName;
    }

    public String getOsUserName() {
        return osUserName;
    }

    private void setOsUserName(String osUserName) {
        this.osUserName = osUserName;
    }

    public String getRegionName() {
        return regionName;
    }

    private void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getUserData() {
        return userData;
    }

    private void setUserData(String userData) {
        this.userData = userData;
    }

    public int getDiskSize() {
        return diskSize;
    }

    private void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    public String getSize() {
        return size;
    }

    private void setSize(String size) {
        this.size = size;
    }

    public static class Builder extends GenericBuilder<AzureCreateVirtualMachineRef> {

        Builder(Supplier instantiator) {
            super(instantiator);
        }

        public Builder azureVirtualMachineImage(AzureGetImageRef azureVirtualMachineImage) {
            with(AzureCreateVirtualMachineRef::setAzureVirtualMachineImage, azureVirtualMachineImage);
            return this;
        }

        public Builder networkInterfaceId(String networkInterfaceId) {
            with(AzureCreateVirtualMachineRef::setNetworkInterfaceId, networkInterfaceId);
            return this;
        }

        public Builder resourceGroupName(String resourceGroupName) {
            with(AzureCreateVirtualMachineRef::setResourceGroupName, resourceGroupName);
            return this;
        }

        public Builder virtualMachineName(String virtualMachineName) {
            with(AzureCreateVirtualMachineRef::setVirtualMachineName, virtualMachineName);
            return this;
        }

        public Builder osUserPassword(String osUserPassword) {
            with(AzureCreateVirtualMachineRef::setOsUserPassword, osUserPassword);
            return this;
        }

        public Builder osComputeName(String osComputeName) {
            with(AzureCreateVirtualMachineRef::setOsComputeName, osComputeName);
            return this;
        }

        public Builder osUserName(String osUserName) {
            with(AzureCreateVirtualMachineRef::setOsUserName, osUserName);
            return this;
        }

        public Builder regionName(String regionName) {
            with(AzureCreateVirtualMachineRef::setRegionName, regionName);
            return this;
        }

        public Builder userData(String userData) {
            with(AzureCreateVirtualMachineRef::setUserData, userData);
            return this;
        }

        public Builder diskSize(int diskSize) {
            with(AzureCreateVirtualMachineRef::setDiskSize, diskSize);
            return this;
        }

        public Builder size(String size) {
            with(AzureCreateVirtualMachineRef::setSize, size);
            return this;
        }
    }
}
