package com.fgan.azure.api;

import com.fgan.azure.api.network.NetworkApiSample;
import com.fgan.azure.util.AzureIDBuilderGeneral;
import com.fgan.azure.util.GeneralPrintUtil;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkSecurityRule;

import java.util.Map;

public class SecurityRuleApi {

    public static void printSecurityRulesFromDefaultSecurityGroup(Azure azure) {
        String securityGroupId = AzureIDBuilderGeneral.buildSecurityGroupId(NetworkApiSample.SECURITY_GROUP_NAME_DEFAULT);
        Map<String, NetworkSecurityRule> stringNetworkSecurityRuleMap =
                azure.networkSecurityGroups().getById(securityGroupId).securityRules();
        for (NetworkSecurityRule networkSecurityRule : stringNetworkSecurityRuleMap.values()) {
            GeneralPrintUtil.printLines(networkSecurityRule::sourcePortRange,
                    networkSecurityRule::destinationPortRange,
                    networkSecurityRule::protocol,
                    networkSecurityRule::direction);
        }
    }

    public static void addSecurityRules(Azure azure) {
        String securityGroupId = AzureIDBuilderGeneral.buildSecurityGroupId(NetworkApiSample.SECURITY_GROUP_NAME_DEFAULT);
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
