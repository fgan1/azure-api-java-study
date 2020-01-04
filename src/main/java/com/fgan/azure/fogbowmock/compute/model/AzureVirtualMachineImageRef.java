package com.fgan.azure.fogbowmock.compute.model;

public class AzureVirtualMachineImageRef {

    private String publisher;
    private String offer;
    private String sku;

    public AzureVirtualMachineImageRef(String publisher, String offer, String sku) {
        this.publisher = publisher;
        this.offer = offer;
        this.sku = sku;
    }

    public String getOffer() {
        return offer;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getSku() {
        return sku;
    }

}
