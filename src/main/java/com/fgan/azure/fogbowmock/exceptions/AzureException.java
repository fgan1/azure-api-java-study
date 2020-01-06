package com.fgan.azure.fogbowmock.exceptions;

public class AzureException {

    public static class Unauthorized extends Exception {
        public Unauthorized() {}

        public Unauthorized(Throwable var2) {
            super(var2);
        }
    }

    public static class ResourceNotFound extends Exception {
        public ResourceNotFound(Throwable var2) {
            super(var2);
        }
    }

    public static class NoAvailableResources extends Exception {
        public NoAvailableResources(String var1) {
            super(var1);
        }
    }

}
