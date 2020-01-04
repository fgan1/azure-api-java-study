package com.fgan.azure.fogbowmock.compute.model;

import com.fgan.azure.fogbowmock.util.GenericBuilder;

import java.util.List;
import java.util.function.Supplier;

public class AzureGetVirtualMachineRef {

    private String id;
    private String cloudState;
    private String name;
    private int vCPU;
    private int memory;
    private int disk;
    private List<String> ipAddresses;

    public static Builder builder() {
        return new Builder(AzureGetVirtualMachineRef::new);
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getCloudState() {
        return cloudState;
    }

    private void setCloudState(String cloudState) {
        this.cloudState = cloudState;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getvCPU() {
        return vCPU;
    }

    private void setvCPU(int vCPU) {
        this.vCPU = vCPU;
    }

    public int getMemory() {
        return memory;
    }

    private void setMemory(int memory) {
        this.memory = memory;
    }

    public int getDisk() {
        return disk;
    }

    private void setDisk(int disk) {
        this.disk = disk;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    private void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public static class Builder extends GenericBuilder<AzureGetVirtualMachineRef> {

        protected Builder(Supplier<AzureGetVirtualMachineRef> instantiator) {
            super(instantiator);
        }

        public Builder id(String id) {
            with(AzureGetVirtualMachineRef::setId, id);
            return this;
        }

        public Builder cloudState(String cloudState) {
            with(AzureGetVirtualMachineRef::setCloudState, cloudState);
            return this;
        }

        public Builder name(String name) {
            with(AzureGetVirtualMachineRef::setName, name);
            return this;
        }

        public Builder vCPU(int vCPU) {
            with(AzureGetVirtualMachineRef::setvCPU, vCPU);
            return this;
        }

        public Builder memory(int memory) {
            with(AzureGetVirtualMachineRef::setMemory, memory);
            return this;
        }

        public Builder disk(int disk) {
            with(AzureGetVirtualMachineRef::setDisk, disk);
            return this;
        }

        public Builder ipAddresses(List<String> ipAddresses) {
            with(AzureGetVirtualMachineRef::setIpAddresses, ipAddresses);
            return this;
        }

    }

}
