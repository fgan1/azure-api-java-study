package com.fgan.azure.fogbowmock;

public class AzureVirtualMachineImage {

    private String publisher;
    private String offer;
    private String sku;

    public AzureVirtualMachineImage(String publisher, String offer, String sku) {
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
