package com.fgan.azure.fogbowmock.compute.model;

import java.util.Objects;

public class AzureGetImageRef {

    private String publisher;
    private String offer;
    private String sku;

    public AzureGetImageRef(String publisher, String offer, String sku) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AzureGetImageRef that = (AzureGetImageRef) o;
        return Objects.equals(publisher, that.publisher) &&
                Objects.equals(offer, that.offer) &&
                Objects.equals(sku, that.sku);
    }
}
