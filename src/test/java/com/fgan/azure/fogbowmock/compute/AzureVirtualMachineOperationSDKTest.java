package com.fgan.azure.fogbowmock.compute;

import ch.qos.logback.classic.Level;
import cloud.fogbow.common.exceptions.FogbowException;
import com.fgan.azure.LoggerAssert;
import com.fgan.azure.fogbowmock.common.AzureCloudUser;
import com.fgan.azure.fogbowmock.common.Messages;
import com.fgan.azure.fogbowmock.compute.model.AzureCreateVirtualMachineRef;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.fogbowmock.util.AzureClientCacheManager;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AzureClientCacheManager.class})
public class AzureVirtualMachineOperationSDKTest {

    private LoggerAssert loggerAssert = new LoggerAssert(AzureVirtualMachineOperationSDK.class);
    private AzureVirtualMachineOperationSDK azureVirtualMachineOperationSDK;
    private AzureCloudUser azureCloudUser;
    private Azure azure;

    @Before
    public void setUp() throws AzureException.Unauthorized, FogbowException {

        this.azureVirtualMachineOperationSDK =
                Mockito.spy(new AzureVirtualMachineOperationSDK());
        this.azureCloudUser = Mockito.mock(AzureCloudUser.class);
        this.azure = Mockito.mock(Azure.class);
//        mockGetAzureClient();
        makeTheObservablesSynchronous();
    }

    // test case:
    @Test
    public void testFindVirtualMachineSizeByNameSuccessfully() {
        // set up
        // exercise
        // verify
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
    public void testDoCreateInstanceSuccessfully() throws AzureException.ResourceNotFound, AzureException.Unauthorized {
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

    private void mockGetAzureClient() throws AzureException.Unauthorized {
        PowerMockito.mockStatic(AzureClientCacheManager.class);
        PowerMockito.when(
                AzureClientCacheManager.getAzure(Mockito.eq(this.azureCloudUser)))
                .thenReturn(azure);
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

}
