package com.fgan.azure.api.network.exceptions;

public class CreateNetworkInterfaceException extends RuntimeException implements CreateNetworkOperationException {

    public CreateNetworkInterfaceException() {
    }

    public CreateNetworkInterfaceException(String message) {
        super(message);
    }

    public CreateNetworkInterfaceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
