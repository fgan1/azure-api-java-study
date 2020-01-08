package com.fgan.azure.fogbowmock.util;

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
    public static String generatePassword() {
        return "AAAaaa111";
    }

}
