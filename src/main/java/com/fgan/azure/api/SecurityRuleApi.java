package com.fgan.azure.api;

import com.fgan.azure.AzureIDBuilder;
import com.fgan.azure.PrintHolder;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkSecurityRule;

import java.util.Map;

public class SecurityRuleApi {

    public static void printSecurityRules(Azure azure) {
        String securityGroupId = AzureIDBuilder.buildSecurityGroupId(NetworkApi.SECURITY_GROUP_NAME_DEFAULT);
        Map<String, NetworkSecurityRule> stringNetworkSecurityRuleMap =
                azure.networkSecurityGroups().getById(securityGroupId).securityRules();
        for (NetworkSecurityRule networkSecurityRule : stringNetworkSecurityRuleMap.values()) {
            PrintHolder.printLines(networkSecurityRule::sourcePortRange,
                    networkSecurityRule::destinationPortRange,
                    networkSecurityRule::protocol,
                    networkSecurityRule::direction);
        }

    }

    public static void addSecurityRules(Azure azure) {
        String securityGroupId = AzureIDBuilder.buildSecurityGroupId(NetworkApi.SECURITY_GROUP_NAME_DEFAULT);
        azure.networkSecurityGroups().getById(securityGroupId).update()
                .defineRule("123")
                .allowInbound()
                .fromAddress("10.10.10.10")
                .fromPort(11)
                .toAddress("9.9.9.9")
                .toPort(22)
                .withAnyProtocol()
                .withPriority(4000)
                .attach().apply();

    }

}
