package com.fgan.azure.fogbowmock.exceptions;

import cloud.fogbow.common.constants.Messages;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnexpectedException;

public class AzureException {

    public static class Unauthenticated extends UnauthenticatedUserException {
        public Unauthenticated() {
            super();
        }

        public Unauthenticated(Throwable throwable) {
            super(Messages.Exception.AUTHENTICATION_ERROR, throwable);
        }
    }

    public static class ResourceNotFound extends InstanceNotFoundException {

        public ResourceNotFound() {
            super(Messages.Exception.INSTANCE_NOT_FOUND);
        }

        public ResourceNotFound(Throwable throwable) {
            super(Messages.Exception.INSTANCE_NOT_FOUND, throwable);
        }

        public ResourceNotFound(String message, Throwable throwable) {
            super(message, throwable);
        }
    }

    public static class Unexpected extends UnexpectedException {
        public Unexpected(Throwable throwable) {
            super(Messages.Exception.UNEXPECTED, throwable);
        }
    }

    public static class NoAvailableResources extends NoAvailableResourcesException {
        public NoAvailableResources(String message, Throwable throwable) {
            super(message, throwable);
        }

        public NoAvailableResources(String message) {
            super(message);
        }
    }

}
