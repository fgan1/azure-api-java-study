package com.fgan.azure.fogbowmock.util;

import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import com.fgan.azure.fogbowmock.common.Messages;
import org.apache.commons.lang3.RandomStringUtils;

public interface AzureGeneralPolicy {

    final int MINIMUM_DISK = 30;
    final int MAXIMUM_NETWORK_PER_VIRTUAL_MACHINE = 1;
    String PASSWORD_PREFIX = "P4ss@";

    /**
     * Azure Password Policy:
     * 1) Contains an uppercase character
     * 2) Contains a lowercase character
     * 3) Contains a numeric digit
     * 4) Contains a special character
     * 5) Control characters are not allowed
     */
    public static String generatePassword() {
        return PASSWORD_PREFIX + RandomStringUtils.randomAlphabetic(PASSWORD_PREFIX.length());
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
