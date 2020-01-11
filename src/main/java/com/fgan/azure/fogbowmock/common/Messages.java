package com.fgan.azure.fogbowmock.common;

import java.util.function.Function;

public class Messages {

    public static final String ERROR_CREATE_VM_ASYNC_BEHAVIOUR = "Error while creating virtual machine asynchonously";
    public static final String START_CREATE_VM_ASYNC_BEHAVIOUR = "Start asynchronous create virtual machine";
    public static final String END_CREATE_VM_ASYNC_BEHAVIOUR = "End asynchronous create virtual machine";

    public static final String START_DELETE_VM_ASYNC_BEHAVIOUR = "Start asynchronous delete virtual machine";
    public static final String END_DELETE_VM_ASYNC_BEHAVIOUR = "End asynchronous delete virtual machine";
    public static final String ERROR_DELETE_VM_ASYNC_BEHAVIOUR = "Error while deleting virtual machine asynchonously";

    public static final String START_DELETE_DISK_ASYNC_BEHAVIOUR = "Start asynchronous delete disk";
    public static final String END_DELETE_DISK_ASYNC_BEHAVIOUR = "End asynchronous delete disk";
    public static final String ERROR_DELETE_DISK_ASYNC_BEHAVIOUR = "Error while deleting disk asynchonously";

    public static final String DISK_PARAMETER_AZURE_POLICY = "The disk size must be greater than 30GB";
}
