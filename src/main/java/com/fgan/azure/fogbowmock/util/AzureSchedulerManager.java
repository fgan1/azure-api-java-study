package com.fgan.azure.fogbowmock.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AzureSchedulerManager {

    private static int VIRTUAL_MACHINE_POOL_SIZE = 3;

    public static ExecutorService getVirtualMachineExecutor() {
        return Executors.newFixedThreadPool(VIRTUAL_MACHINE_POOL_SIZE);
    }
}
