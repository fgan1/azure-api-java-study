package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;

public interface AzureGeneralPolicy {

    /**
     * Azure Password Policy:
     * 1) Contains an uppercase character
     * 2) Contains a lowercase character
     * 3) Contains a numeric digit
     * 4) Contains a special character
     * 5) Control characters are not allowed
     */
    // TODO(chico) - finish
    // TODO(chico) - implement tests
    public static String generatePassword() {
        return "AAAaaa111";
    }

    /**
     * Azure Disk Policy
     * 1) Greater then 30GB
     */
    // TODO(chico) - confirm this information
    // TODO(chico) - implement tests
    public static int getDisk(ComputeOrder computeOrder) throws InvalidParameterException {
        int disk = computeOrder.getDisk();
        // TODO (chico) - Add in the constans
        if (disk < 30) throw new InvalidParameterException("The disk size must be greater than 30GB");

        return disk;
    }

}
