package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.common.Messages;

public interface AzureGeneralPolicy {

    final int MINIMUM_DISK = 30;
    final int MAXIMUM_NETWORK_PER_VIRTUAL_MACHINE = 1;

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
     * 1) Greater than 30GB
     */
    public static int getDisk(ComputeOrder computeOrder) throws InvalidParameterException {
        int disk = computeOrder.getDisk();
        if (disk < MINIMUM_DISK) {
            throw new InvalidParameterException(Messages.DISK_PARAMETER_AZURE_POLICY);
        }

        return disk;
    }

}
