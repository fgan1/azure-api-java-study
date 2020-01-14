package com.fgan.azure.fogbowmock.compute;

import cloud.fogbow.common.exceptions.UnexpectedException;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.compute.VirtualMachineSizes;
import com.microsoft.azure.management.compute.VirtualMachines;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;

import java.util.Optional;
import java.util.function.Function;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AzureVirtualMachineSDK.class})
public class AzureVirtualMachineSDKTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // test case: When calling the buildVirtualMachineObservable method with an image linux,
    // it must verify if It returns the right observable.
    @Test
    public void testBuildVirtualMachineObservableSuccessfullyWhenLinx() throws Exception {
        String imageReference = "linux";
        String osUserName = "osUserName";
        String osUserPassword = "osUserPassword";
        String osComputeName = "osComputeName";
        Function<VirtualMachine.DefinitionStages.WithOS, VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged>
                linuxMock = (withExistingPrimaryNetworkInterface) -> {

            VirtualMachine.DefinitionStages.WithLinuxRootUsernameManagedOrUnmanaged withLatestLinuxImage =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithLinuxRootUsernameManagedOrUnmanaged.class);
            Mockito.when(withExistingPrimaryNetworkInterface.withLatestLinuxImage(
                    Mockito.eq(imageReference), Mockito.eq(imageReference), Mockito.eq(imageReference)))
                    .thenReturn(withLatestLinuxImage);

            VirtualMachine.DefinitionStages.WithLinuxRootPasswordOrPublicKeyManagedOrUnmanaged withRootUsername =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithLinuxRootPasswordOrPublicKeyManagedOrUnmanaged.class);
            Mockito.when(withLatestLinuxImage.withRootUsername(Mockito.eq(osUserName)))
                    .thenReturn(withRootUsername);

            VirtualMachine.DefinitionStages.WithLinuxCreateManagedOrUnmanaged withRootPassword =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithLinuxCreateManagedOrUnmanaged.class);
            Mockito.when(withRootUsername.withRootPassword(Mockito.eq(osUserPassword)))
                    .thenReturn(withRootPassword);

            VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged withComputerName =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged.class);
            Mockito.when(withRootPassword.withComputerName(osComputeName)).thenReturn(withComputerName);

            return withComputerName;
        };

        checkBuildVirtualMachineObservable(imageReference , osUserName, osUserPassword, osComputeName, linuxMock);
    }


    // test case: When calling the buildVirtualMachineObservable method with an image windows,
    // it must verify if It returns the right observable.
    @Test
    public void testBuildVirtualMachineObservableSuccessfullyWhenWindows() throws Exception {
        String imageReference = "windows";
        String osUserName = "osUserName";
        String osUserPassword = "osUserPassword";
        String osComputeName = "osComputeName";
        Function<VirtualMachine.DefinitionStages.WithOS, VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged>
                windowsMock = (withExistingPrimaryNetworkInterface) -> {

            VirtualMachine.DefinitionStages.WithWindowsAdminUsernameManagedOrUnmanaged withLatestWindowsImage =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithWindowsAdminUsernameManagedOrUnmanaged.class);
            Mockito.when(withExistingPrimaryNetworkInterface.withLatestWindowsImage(
                    Mockito.eq(imageReference), Mockito.eq(imageReference), Mockito.eq(imageReference)))
                    .thenReturn(withLatestWindowsImage);

            VirtualMachine.DefinitionStages.WithWindowsAdminPasswordManagedOrUnmanaged withAdminUsername =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithWindowsAdminPasswordManagedOrUnmanaged.class);
            Mockito.when(withLatestWindowsImage.withAdminUsername(Mockito.eq(osUserName)))
                    .thenReturn(withAdminUsername);

            VirtualMachine.DefinitionStages.WithWindowsCreateManagedOrUnmanaged withAdminPassword =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithWindowsCreateManagedOrUnmanaged.class);
            Mockito.when(withAdminUsername.withAdminPassword(Mockito.eq(osUserPassword)))
                    .thenReturn(withAdminPassword);

            VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged withComputerName =
                    Mockito.mock(VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged.class);
            Mockito.when(withAdminPassword.withComputerName(osComputeName)).thenReturn(withComputerName);
            return withComputerName;
        };

        checkBuildVirtualMachineObservable(imageReference , osUserName, osUserPassword, osComputeName, windowsMock);
    }

    // test case: When calling the constainsWindownsOn method and imagesku has the windows in the text,
    // it must verify if It returns true.
    @Test
    public void testTsWindowsImageSuccessfullyWhenImageSkuHasWindows() {
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
    public void testIsWindowsImageSuccessfullyWhenImageOfferHasWindows() {
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
    public void testIsWindowsImageSuccessfullyWhenItIsNotWindows() {
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
    public void testConstainsWindownsOnSuccessfullyWhenContainsLowerCase() {
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
    public void testConstainsWindownsOnSuccessfullyWhenContainsUpperCase() {
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
    public void testConstainsWindownsOnSuccessfullyWhenContainsLongText() {
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
    public void testConstainsWindownsOnSuccessfullyWhenNotContains() {
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
    public void testConstainsWindownsOnSuccessfullyWhenNotContainsWitSimilarText() {
        // set up
        String text = "linux-wind.wos-WINDOW-S";
        // exercise
        boolean containsWindows = AzureVirtualMachineSDK.constainsWindownsOn(text);
        // verify
        Assert.assertFalse(containsWindows);
    }

    // test case: When calling the getVirtualMachineById method and finds a virtual machine,
    // it must verify if It returns a Optional with a virtual machine.
    @Test
    public void testGetVirtualMachineByIdSuccessfullyWhenFindVirtualMachine() throws Exception {
        // set up
        Azure azure = null;
        String virtualMachineId = "virtualMachineId";
        VirtualMachines virtualMachineObject = Mockito.mock(VirtualMachines.class);
        VirtualMachine virtualMachine = Mockito.mock(VirtualMachine.class);
        Mockito.when(virtualMachineObject.getById(Mockito.eq(virtualMachineId)))
                .thenReturn(virtualMachine);
        PowerMockito.spy(AzureVirtualMachineSDK.class);
        PowerMockito.doReturn(virtualMachineObject)
                .when(AzureVirtualMachineSDK.class, "getVirtualMachinesObject", Mockito.eq(azure));

        // exercise
        Optional<VirtualMachine> virtualMachineOptional =
                AzureVirtualMachineSDK.getVirtualMachineById(azure, virtualMachineId);

        // verify
        Assert.assertTrue(virtualMachineOptional.isPresent());
        Assert.assertEquals(virtualMachine, virtualMachineOptional.get());
    }

    // test case: When calling the getVirtualMachineById method and do not find a virtual machine,
    // it must verify if It returns a Optional with a virtual machine.
    @Test
    public void testGetVirtualMachineByIdSuccessfullyWhenNotFindVirtualMachine() throws Exception {
        // set up
        Azure azure = null;
        String virtualMachineId = "virtualMachineId";
        VirtualMachines virtualMachineObject = Mockito.mock(VirtualMachines.class);
        VirtualMachine virtualMachineNull = null;
        Mockito.when(virtualMachineObject.getById(Mockito.eq(virtualMachineId)))
                .thenReturn(virtualMachineNull);
        PowerMockito.spy(AzureVirtualMachineSDK.class);
        PowerMockito.doReturn(virtualMachineObject)
                .when(AzureVirtualMachineSDK.class, "getVirtualMachinesObject", Mockito.eq(azure));

        // exercise
        Optional<VirtualMachine> virtualMachineOptional =
                AzureVirtualMachineSDK.getVirtualMachineById(azure, virtualMachineId);

        // verify
        Assert.assertFalse(virtualMachineOptional.isPresent());
    }

    // test case: When calling the getVirtualMachineById method and throws any exception,
    // it must verify if It throws an UnexpectedException.
    @Test
    public void testGetVirtualMachineByIdFail() throws Exception {
        // set up
        Azure azure = null;
        String virtualMachineId = "virtualMachineId";
        String errorMessage = "error";
        PowerMockito.spy(AzureVirtualMachineSDK.class);
        PowerMockito.doThrow(new RuntimeException(errorMessage))
                .when(AzureVirtualMachineSDK.class, "getVirtualMachinesObject", Mockito.eq(azure));

        // verify
        this.expectedException.expect(UnexpectedException.class);
        this.expectedException.expectMessage(errorMessage);

        // exercise
        AzureVirtualMachineSDK.getVirtualMachineById(azure, virtualMachineId);
    }

    // test case: When calling the getVirtualMachineSizes method,
    // it must verify if It returns a list of virtual machine sizes.
    @Test
    public void testGetVirtualMachineSizesSuccessfully() throws Exception {
        // set up
        Azure azure = null;
        Region region = Region.US_EAST;

        VirtualMachines virtualMachineObject = Mockito.mock(VirtualMachines.class);
        PowerMockito.spy(AzureVirtualMachineSDK.class);
        VirtualMachineSizes sizes = Mockito.mock(VirtualMachineSizes.class);
        PagedList<VirtualMachineSize> virtualMachineSizeExpected = Mockito.mock(PagedList.class);
        Mockito.when(sizes.listByRegion(Mockito.eq(region))).thenReturn(virtualMachineSizeExpected);
        Mockito.when(virtualMachineObject.sizes()).thenReturn(sizes);
        PowerMockito.doReturn(virtualMachineObject)
                .when(AzureVirtualMachineSDK.class, "getVirtualMachinesObject", Mockito.eq(azure));

        // exercise
        PagedList<VirtualMachineSize> virtualMachineSizes =
                AzureVirtualMachineSDK.getVirtualMachineSizes(azure, region);

        // verify
        Assert.assertEquals(virtualMachineSizeExpected, virtualMachineSizes);
    }

    // test case: When calling the getVirtualMachineSizes method ant throws an exception,
    // it must verify if It throws a UnexpectedException.
    @Test
    public void testGetVirtualMachineFail() throws Exception {
        // set up
        Azure azure = null;
        Region region = Region.US_EAST;

        String errorMessage = "error";
        PowerMockito.spy(AzureVirtualMachineSDK.class);
        PowerMockito.doThrow(new RuntimeException(errorMessage))
                .when(AzureVirtualMachineSDK.class, "getVirtualMachinesObject", Mockito.eq(azure));

        // verify
        this.expectedException.expect(UnexpectedException.class);
        this.expectedException.expectMessage(errorMessage);

        // exercise
        AzureVirtualMachineSDK.getVirtualMachineSizes(azure, region);

    }


    private void checkBuildVirtualMachineObservable(String imageReference, String osUserName, String osUserPassword, String osComputeName,
                                                    Function<VirtualMachine.DefinitionStages.WithOS, VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged> function)
            throws Exception {

        // verify
        Azure azure = null;
        String virtualMachineName = "virtualMachineName";
        Region region = Region.US_EAST;
        String resourceGroupName = "resourceGroupName";
        NetworkInterface networkInterface = Mockito.mock(NetworkInterface.class);
        String imagePublished = imageReference;
        String imageSku = imageReference;
        String imageOffer = imageReference;
        String userData = "userData";
        int diskSize = 1;
        String size = "size";

        PowerMockito.spy(AzureVirtualMachineSDK.class);

        VirtualMachines virtualMachine = Mockito.mock(VirtualMachines.class);

        VirtualMachine.DefinitionStages.Blank define = Mockito.mock(VirtualMachine.DefinitionStages.Blank.class);
        Mockito.when(virtualMachine.define(Mockito.eq(virtualMachineName))).thenReturn(define);

        VirtualMachine.DefinitionStages.WithGroup withRegion = Mockito.mock(VirtualMachine.DefinitionStages.WithGroup.class);
        Mockito.when(define.withRegion(Mockito.eq(region))).thenReturn(withRegion);

        VirtualMachine.DefinitionStages.WithNetwork withExistingResourceGroup
                = Mockito.mock(VirtualMachine.DefinitionStages.WithNetwork.class);
        Mockito.when(withRegion.withExistingResourceGroup(Mockito.eq(resourceGroupName))).thenReturn(withExistingResourceGroup);

        VirtualMachine.DefinitionStages.WithOS withExistingPrimaryNetworkInterface
                = Mockito.mock(VirtualMachine.DefinitionStages.WithOS.class);
        Mockito.when(withExistingResourceGroup.withExistingPrimaryNetworkInterface(Mockito.eq(networkInterface)))
                .thenReturn(withExistingPrimaryNetworkInterface);

        VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged withComputerName
                = function.apply(withExistingPrimaryNetworkInterface);

        VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged withCustomData
                = Mockito.mock(VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged.class);
        Mockito.when(withComputerName.withCustomData(Mockito.eq(userData)))
                .thenReturn(withCustomData);

        VirtualMachine.DefinitionStages.WithCreate withOSDiskSizeInGB
                = Mockito.mock(VirtualMachine.DefinitionStages.WithCreate.class);
        Mockito.when(withCustomData.withOSDiskSizeInGB(diskSize)).thenReturn(withOSDiskSizeInGB);

        VirtualMachine.DefinitionStages.WithCreate withSize
                = Mockito.mock(VirtualMachine.DefinitionStages.WithCreate.class);
        Mockito.when(withOSDiskSizeInGB.withSize(size)).thenReturn(withSize);

        Observable<Indexable> observableExpected = Mockito.mock(Observable.class);
        Mockito.when(withSize.createAsync()).thenReturn(observableExpected);

        PowerMockito.doReturn(virtualMachine)
                .when(AzureVirtualMachineSDK.class, "getVirtualMachinesObject", Mockito.eq(azure));

        // exercise
        Observable<Indexable> observable = AzureVirtualMachineSDK.buildVirtualMachineObservable(
                azure, virtualMachineName, region, resourceGroupName, networkInterface,
                imagePublished, imageOffer, imageSku, osUserName, osUserPassword,
                osComputeName, userData, diskSize, size);

        // verify
        Assert.assertEquals(observableExpected, observable);
    }

}
