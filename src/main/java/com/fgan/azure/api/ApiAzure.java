package com.fgan.azure.api;

import com.microsoft.azure.management.Azure;

public class ApiAzure {

    protected Azure azure;

    public ApiAzure(Azure azure) {
        this.azure = azure;
    }

}
