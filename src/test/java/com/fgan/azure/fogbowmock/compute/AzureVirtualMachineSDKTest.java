package com.fgan.azure.fogbowmock.compute;

import org.junit.Assert;
import org.junit.Test;

public class AzureVirtualMachineSDKTest {

    // test case: When calling the constainsWindownsOn method and imagesku has the windows in the text,
    // it must verify if It returns true.
    @Test
    public void isWindowsImageSuccessfullyWhenImageSkuHasWindows() {
        // set up
        String imageSku = "windows";
        String imageOffer = "System";

        // exercise
        boolean isWindows = AzureVirtualMachineSDK.isWindowsImage(imageOffer, imageSku);
        // verify
        Assert.assertTrue(isWindows);
    }

    // test case: When calling the constainsWindownsOn method and imageoffer has the windows in the text,
    // it must verify if It returns true.
    @Test
    public void isWindowsImageSuccessfullyWhenImageOfferHasWindows() {
        // set up
        String imageSku = "10.20";
        String imageOffer = "windows";

        // exercise
        boolean isWindows = AzureVirtualMachineSDK.isWindowsImage(imageOffer, imageSku);
        // verify
        Assert.assertTrue(isWindows);
    }

    // test case: When calling the constainsWindownsOn method and neither sku or offer has the
    // windows in the text, it must verify if It returns false.
    @Test
    public void isWindowsImageSuccessfullyWhenItIsNotWindows() {
        // set up
        String imageSku = "linux";
        String imageOffer = "system";

        // exercise
        boolean isWindows = AzureVirtualMachineSDK.isWindowsImage(imageOffer, imageSku);
        // verify
        Assert.assertFalse(isWindows);
    }

    // test case: When calling the constainsWindownsOn method and it contains windows lower case,
    // it must verify if It returns true.
    @Test
    public void constainsWindownsOnSuccessfullyWhenContainsLowerCase() {
        // set up
        String text = "windows";
        // exercise
        boolean containsWindows = AzureVirtualMachineSDK.constainsWindownsOn(text);
        // verify
        Assert.assertTrue(containsWindows);
    }

    // test case: When calling the constainsWindownsOn method and it contains windows upper case,
    // it must verify if It returns true.
    @Test
    public void constainsWindownsOnSuccessfullyWhenContainsUpperCase() {
        // set up
        String text = "WINDOWS";
        // exercise
        boolean containsWindows = AzureVirtualMachineSDK.constainsWindownsOn(text);
        // verify
        Assert.assertTrue(containsWindows);
    }

    // test case: When calling the constainsWindownsOn method and it contains windows in a long text,
    // it must verify if It returns true.
    @Test
    public void constainsWindownsOnSuccessfullyWhenContainsLongText() {
        // set up
        String text = "abc - windows WINDOWS  1204.65.78.9.8.7.86 _()}Ç:,vm";
        // exercise
        boolean containsWindows = AzureVirtualMachineSDK.constainsWindownsOn(text);
        // verify
        Assert.assertTrue(containsWindows);
    }

    // test case: When calling the constainsWindownsOn method and it does not contains windows,
    // it must verify if It returns false.
    @Test
    public void constainsWindownsOnSuccessfullyWhenNotContains() {
        // set up
        String text = "linux-v2";
        // exercise
        boolean containsWindows = AzureVirtualMachineSDK.constainsWindownsOn(text);
        // verify
        Assert.assertFalse(containsWindows);
    }

    // test case: When calling the constainsWindownsOn method and it does not contains windows but
    // the text is similar, it must verify if It returns false.
    @Test
    public void constainsWindownsOnSuccessfullyWhenNotContainsWitSimilarText() {
        // set up
        String text = "linux-wind.wos-WINDOW-S";
        // exercise
        boolean containsWindows = AzureVirtualMachineSDK.constainsWindownsOn(text);
        // verify
        Assert.assertFalse(containsWindows);
    }

}
