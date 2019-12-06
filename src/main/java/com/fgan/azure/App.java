package com.fgan.azure;

import com.fgan.azure.api.Compute;
import com.fgan.azure.api.Identity;
import com.microsoft.azure.management.Azure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws Exception {
        LOGGER.info("Hello Azure!");

        Azure azure = Identity.getAzure();
        Compute.runSambleOne(azure);
    }
}
