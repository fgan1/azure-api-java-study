package com.fgan.azure.fogbowmock.compute;

import ch.qos.logback.classic.Level;
import com.fgan.azure.LoggerAssert;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.Messages;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.microsoft.azure.Page;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import com.microsoft.rest.RestException;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AzureClientCacheManager.class, AzureVirtualMachineSDK.class})
public class AzureVirtualMachineOperationSDKTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private LoggerAssert loggerAssert = new LoggerAssert(AzureVirtualMachineOperationSDK.class);
    private AzureVirtualMachineOperationSDK azureVirtualMachineOperationSDK;
    private AzureCloudUser azureCloudUser;
    private Azure azure;

    @Before
    public void setUp() throws AzureException.Unauthenticated {
        this.azureVirtualMachineOperationSDK =
                Mockito.spy(new AzureVirtualMachineOperationSDK());
        this.azureCloudUser = Mockito.mock(AzureCloudUser.class);
        this.azure = null;
        makeTheObservablesSynchronous();
    }

    // test case: When calling the findVirtualMachineSizeByName method and find one the virtual machine size,
    // it must verify if It returns the right virtual machine size.
    @Test
    public void testFindVirtualMachineSizeByNameSuccessfully()
            throws AzureException.Unauthenticated, AzureException.NoAvailableResources, AzureException.Unexpected {
        // set up
        mockGetAzureClient();
        String virtualMachineSizeNameExpected = "nameExpected";

        String regionName = Region.US_EAST.name();
        PagedList<VirtualMachineSize> virtualMachines = getVirtualMachineSizesMock();

        VirtualMachineSize virtualMachineSizeNotMactchOne = buildVirtualMachineSizeMock("notmatch");
        VirtualMachineSize virtualMachineSizeMatch = buildVirtualMachineSizeMock(virtualMachineSizeNameExpected);
        VirtualMachineSize virtualMachineSizeNotMactchTwo = buildVirtualMachineSizeMock("notmatch");

        virtualMachines.add(virtualMachineSizeNotMactchOne);
        virtualMachines.add(virtualMachineSizeMatch);
        virtualMachines.add(virtualMachineSizeNotMactchTwo);

        PowerMockito.mockStatic(AzureVirtualMachineSDK.class);
        PowerMockito.when(AzureVirtualMachineSDK.getVirtualMachineSizes(Mockito.any(), Mockito.any()))
                .thenReturn(virtualMachines);

        // exercise
        VirtualMachineSize virtualMachineSize = this.azureVirtualMachineOperationSDK
                .findVirtualMachineSizeByName(virtualMachineSizeNameExpected, regionName, this.azure);

        // verify
        Assert.assertEquals(virtualMachineSizeMatch.name(), virtualMachineSize.name());
    }

    // test case: When calling the findVirtualMachineSizeByName method and does not find the virtual machine size,
    // it must verify if It throws a NoAvailableResourcesException exception.
    @Test
    public void testFindVirtualMachineSizeByNameFail()
            throws AzureException.Unauthenticated, AzureException.NoAvailableResources, AzureException.Unexpected {
        // set up
        mockGetAzureClient();
        String virtualMachineSizeNameExpected = "nameExpected";

        String regionName = Region.US_EAST.name();
        PagedList<VirtualMachineSize> virtualMachines = getVirtualMachineSizesMock();

        VirtualMachineSize virtualMachineSizeNotMactchOne = buildVirtualMachineSizeMock("notmatch");
        VirtualMachineSize virtualMachineSizeNotMactchTwo = buildVirtualMachineSizeMock("notmatch");

        virtualMachines.add(virtualMachineSizeNotMactchOne);
        virtualMachines.add(virtualMachineSizeNotMactchTwo);

        PowerMockito.mockStatic(AzureVirtualMachineSDK.class);
        PowerMockito.when(AzureVirtualMachineSDK.getVirtualMachineSizes(Mockito.any(), Mockito.any()))
                .thenReturn(virtualMachines);

        // verify
        this.expectedException.expect(AzureException.NoAvailableResources.class);

        // exercise
        this.azureVirtualMachineOperationSDK
                .findVirtualMachineSizeByName(virtualMachineSizeNameExpected, regionName, this.azure);
    }


    // test case: When calling the findVirtualMachineSize method with two virtual machine size that
    // fits in the requirements, it must verify if It returns the smaller virtual machine size.
    @Test
    public void testFindVirtualMachineSizeSuccessfully()
            throws AzureException.NoAvailableResources, AzureException.Unauthenticated, AzureException.Unexpected {

        // set up
        mockGetAzureClient();

        int memory = 1;
        int vcpu = 2;
        String regionName = Region.US_EAST.name();

        PagedList<VirtualMachineSize> virtualMachines = getVirtualMachineSizesMock();

        int lessThanMemoryRequired = memory - 1;
        int lessThanCpuRequired = vcpu - 1;
        VirtualMachineSize virtualMachineSizeNotFits = buildVirtualMachineSizeMock(lessThanMemoryRequired, lessThanCpuRequired);
        VirtualMachineSize virtualMachineSizeFitsSmaller = buildVirtualMachineSizeMock(memory, vcpu);
        VirtualMachineSize virtualMachineSizeFitsBigger = buildVirtualMachineSizeMock(Integer.MAX_VALUE, Integer.MAX_VALUE);

        virtualMachines.add(virtualMachineSizeNotFits);
        virtualMachines.add(virtualMachineSizeFitsSmaller);
        virtualMachines.add(virtualMachineSizeFitsBigger);

        PowerMockito.mockStatic(AzureVirtualMachineSDK.class);
        PowerMockito.when(AzureVirtualMachineSDK.getVirtualMachineSizes(Mockito.any(), Mockito.any()))
                .thenReturn(virtualMachines);

        // exercise
        String virtualMachineSize = this.azureVirtualMachineOperationSDK
                .findVirtualMachineSize(memory, vcpu, regionName, this.azureCloudUser);

        // verify
        Assert.assertNotEquals(virtualMachineSizeFitsBigger.name(), virtualMachineSize);
        Assert.assertEquals(virtualMachineSizeFitsSmaller.name(), virtualMachineSize);
    }

    // test case: When calling the findVirtualMachineSize method with any virtual machine size that
    // fits in the requirements, it must verify if It throws a NoAvailableResourcesException.
    @Test
    public void testFindVirtualMachineSizeFail()
            throws AzureException.NoAvailableResources, AzureException.Unauthenticated, AzureException.Unexpected {

        // set up
        mockGetAzureClient();

        int memory = 1;
        int vcpu = 2;
        String regionName = Region.US_EAST.name();

        PagedList<VirtualMachineSize> virtualMachines = getVirtualMachineSizesMock();

        int lessThanMemoryRequired = memory - 1;
        int lessThanCpuRequired = vcpu - 1;
        VirtualMachineSize virtualMachineSizeNotFits = buildVirtualMachineSizeMock(lessThanMemoryRequired, lessThanCpuRequired);
        virtualMachines.add(virtualMachineSizeNotFits);

        PowerMockito.mockStatic(AzureVirtualMachineSDK.class);
        PowerMockito.when(AzureVirtualMachineSDK.getVirtualMachineSizes(Mockito.any(), Mockito.any()))
                .thenReturn(virtualMachines);

        // verify
        this.expectedException.expect(AzureException.NoAvailableResources.class);

        // exercise
        this.azureVirtualMachineOperationSDK.findVirtualMachineSize(memory, vcpu, regionName, this.azureCloudUser);
    }

    // test case: When calling the findVirtualMachineSize method with throw an Unauthorized
    // exception, it must verify if It throws an Unauthorized exception.
    @Test
    public void testFindVirtualMachineSizeFailWhenThrowUnauthorized()
            throws AzureException.NoAvailableResources, AzureException.Unauthenticated, AzureException.Unexpected {

        // set up
        mockGetAzureClientUnauthorized();
        int memory = 1;
        int vcpu = 2;
        String regionName = Region.US_EAST.name();

        // verify
        this.expectedException.expect(AzureException.Unauthenticated.class);

        // exercise
        this.azureVirtualMachineOperationSDK.findVirtualMachineSize(memory, vcpu, regionName, this.azureCloudUser);
    }

    // test case: When calling the subscribeDeleteVirtualMachine method and the completable executes
    // without any error, it must verify if It returns the right logs.
    @Test
    public void testSubscribeDeleteVirtualMachineSuccessfully() {
        // set up
        Completable virtualMachineCompletable = createSimpleCompletableSuccess();

        // exercise
        this.azureVirtualMachineOperationSDK.subscribeDeleteVirtualMachine(virtualMachineCompletable);

        // verify
        this.loggerAssert
                .assertEqualsInOrder(Level.INFO, Messages.START_DELETE_VM_ASYNC_BEHAVIOUR)
                .assertEqualsInOrder(Level.INFO, Messages.END_DELETE_VM_ASYNC_BEHAVIOUR);
    }

    // test case: When calling the subscribeDeleteVirtualMachine method and the completable executes
    // with an error, it must verify if It returns the right logs.
    @Test
    public void testSubscribeDeleteVirtualMachineFail() {
        // set up
        Completable virtualMachineCompletable = createSimpleCompletableFail();

        // exercise
        this.azureVirtualMachineOperationSDK.subscribeDeleteVirtualMachine(virtualMachineCompletable);

        // verify
        this.loggerAssert
                .assertEqualsInOrder(Level.INFO, Messages.START_DELETE_VM_ASYNC_BEHAVIOUR)
                .assertEqualsInOrder(Level.ERROR, Messages.ERROR_DELETE_VM_ASYNC_BEHAVIOUR);
    }

    // test case: When calling the subscribeCreateVirtualMachine method and the observable executes
    // without any error, it must verify if It returns the right logs.
    @Test
    public void testSubscribeCreateVirtualMachineSuccessfully() {
        // set up
        Observable<Indexable> virtualMachineObservable = createSimpleObservableSuccess();

        // exercise
        this.azureVirtualMachineOperationSDK.subscribeCreateVirtualMachine(virtualMachineObservable);

        // verify
        this.loggerAssert
                .assertEqualsInOrder(Level.INFO, Messages.START_CREATE_VM_ASYNC_BEHAVIOUR)
                .assertEqualsInOrder(Level.INFO, Messages.END_CREATE_VM_ASYNC_BEHAVIOUR);
    }

    // test case: When calling the subscribeCreateVirtualMachine method and the observable executes
    // with an error, it must verify if It returns the right logs.
    @Test
    public void testSubscribeCreateVirtualMachineFail() {
        // set up
        Observable<Indexable> virtualMachineObservable = createSimpleObservableFail();

        // exercise
        this.azureVirtualMachineOperationSDK.subscribeCreateVirtualMachine(virtualMachineObservable);

        // verify
        this.loggerAssert
                .assertEqualsInOrder(Level.INFO, Messages.START_CREATE_VM_ASYNC_BEHAVIOUR)
                .assertEqualsInOrder(Level.ERROR, Messages.ERROR_CREATE_VM_ASYNC_BEHAVIOUR);
    }

    @Ignore
    @Test
    public void testDoCreateInstanceSuccessfully()
            throws AzureException.Unauthenticated, AzureException.ResourceNotFound {

        // set up
        AzureCreateVirtualMachineRef azureCreateVirtualMachineRef = AzureCreateVirtualMachineRef.builder()
                .build();

        Observable<Indexable> observableMocked = Mockito.mock(Observable.class);
        Mockito.doReturn(observableMocked).when(this.azureVirtualMachineOperationSDK).getAzureVirtualMachineObservable(
                Mockito.eq(azureCreateVirtualMachineRef), Mockito.eq(this.azure));

        Mockito.doNothing().when(this.azureVirtualMachineOperationSDK)
                .subscribeCreateVirtualMachine(Mockito.any());

        // exercise
        this.azureVirtualMachineOperationSDK.doCreateInstance(azureCreateVirtualMachineRef, this.azureCloudUser);

        // verify
        Mockito.verify(this.azureVirtualMachineOperationSDK, Mockito.times(1))
                .subscribeCreateVirtualMachine(Mockito.eq(observableMocked));
    }

    @NotNull
    private PagedList<VirtualMachineSize> getVirtualMachineSizesMock() {
        return new PagedList<VirtualMachineSize>() {
            @Override
            public Page<VirtualMachineSize> nextPage(String s) throws RestException {
                return null;
            }
        };
    }

    private void mockGetAzureClient() throws AzureException.Unauthenticated {
        PowerMockito.mockStatic(AzureClientCacheManager.class);
        PowerMockito.when(AzureClientCacheManager.getAzure(Mockito.eq(this.azureCloudUser))).thenReturn(azure);
    }

    private void mockGetAzureClientUnauthorized() throws AzureException.Unauthenticated {
        PowerMockito.mockStatic(AzureClientCacheManager.class);
        PowerMockito.when(AzureClientCacheManager.getAzure(Mockito.eq(this.azureCloudUser)))
                .thenThrow(new AzureException.Unauthenticated());
    }

    // TODO(chico) - Move it to a utils Class
    private void makeTheObservablesSynchronous() {
        // The scheduler trampolime makes the subscriptions execute in the current thread
        Mockito.doReturn(Schedulers.trampoline())
                .when(this.azureVirtualMachineOperationSDK).getScheduler();
    }

    private Completable createSimpleCompletableSuccess() {
        return Completable.complete();
    }

    private Completable createSimpleCompletableFail() {
        return Completable.error(new RuntimeException());
    }

    private Observable<Indexable> createSimpleObservableSuccess() {
        return Observable.defer(() -> {
            Indexable indexable = Mockito.mock(Indexable.class);
            return Observable.just(indexable);
        });
    }

    private Observable<Indexable> createSimpleObservableFail() {
        return Observable.defer(() -> {
            throw new RuntimeException();
        });
    }

    private VirtualMachineSize buildVirtualMachineSizeMock(int memory, int vcpu) {
        String name = RandomStringUtils.randomAlphabetic(10);
        return buildVirtualMachineSizeMock(memory, vcpu, name);
    }

    private VirtualMachineSize buildVirtualMachineSizeMock(String name) {
        return buildVirtualMachineSizeMock(0, 0, name);
    }

    private VirtualMachineSize buildVirtualMachineSizeMock(int memory, int vcpu, String name) {
        VirtualMachineSize virtualMachineSize = Mockito.mock(VirtualMachineSize.class);
        Mockito.when(virtualMachineSize.memoryInMB()).thenReturn(memory);
        Mockito.when(virtualMachineSize.numberOfCores()).thenReturn(vcpu);
        Mockito.when(virtualMachineSize.name()).thenReturn(name);
        return virtualMachineSize;
    }

}
