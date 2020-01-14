package com.fgan.azure.fogbowmock.common;

public class Messages {

    public static final String ERROR_CREATE_VM_ASYNC_BEHAVIOUR = "Error while creating virtual machine asynchonously";
    public static final String END_CREATE_VM_ASYNC_BEHAVIOUR = "End asynchronous create virtual machine";

    public static final String END_DELETE_VM_ASYNC_BEHAVIOUR = "End asynchronous delete virtual machine";
    public static final String ERROR_DELETE_VM_ASYNC_BEHAVIOUR = "Error while deleting virtual machine asynchonously";

    public static final String END_DELETE_DISK_ASYNC_BEHAVIOUR = "End asynchronous delete disk";
    public static final String ERROR_DELETE_DISK_ASYNC_BEHAVIOUR = "Error while deleting disk asynchonously";

    public static final String DISK_PARAMETER_AZURE_POLICY = "The disk size must be greater than 30GB";
    public static final String MULTIPLE_NETWORKS_NOT_ALLOWED = "Multiple networks not allowed";

    public static final String CREATE_NEW_AZURE_CLIENTE = "Creating a new Azure client";
    public static final String ERROR_ID_LIMIT_SIZE_EXCEEDED = "The resource name exceeded %s characters of the limit";
    public static final String SEEK_VIRTUAL_MACHINE_SIZE_NAME =
            "Seek for the Virtual Machine Size that fits with memory(%s) and vCpu(%s) at region %s";
    public static final String SEEK_VIRTUAL_MACHINE_SIZE_BY_NAME =
            "Seek for the Virtual Machine Size by name %s at region %s";
}
