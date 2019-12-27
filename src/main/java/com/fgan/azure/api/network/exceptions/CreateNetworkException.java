package com.fgan.azure.api.network.exceptions;

public class CreateNetworkException extends RuntimeException implements CreateNetworkOperationException{

    public CreateNetworkException() {}

    public CreateNetworkException(String message) {
        super(message);
    }

    public CreateNetworkException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
