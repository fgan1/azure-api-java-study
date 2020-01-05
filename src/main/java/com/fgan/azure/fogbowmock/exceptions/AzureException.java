package com.fgan.azure.fogbowmock.exceptions;

import cloud.fogbow.common.exceptions.NoAvailableResourcesException;

public class AzureException {

    public static class Unauthorized extends Exception {
        public Unauthorized(Throwable var2) {
            super(var2);
        }
    }

    public static class ResourceNotFound extends Exception {
        public ResourceNotFound(Throwable var2) {
            super(var2);
        }
    }

    public static class NoAvailableResourcesException extends Exception {
        public NoAvailableResourcesException(String var1) {
            super(var1);
        }
    }

    public static class ParamaterRequiredException extends Exception {
        public ParamaterRequiredException(String var1) {
            super(var1);
        }
    }

}
