package com.fgan.azure.fogbowmock.util;

public class GenericBuilderException extends Exception {

    public static String FIELD_REQUIRED_MESSAGE = "The field %s is required in the class %s.";

    public GenericBuilderException(String var1) {
        super(var1);
    }
}
