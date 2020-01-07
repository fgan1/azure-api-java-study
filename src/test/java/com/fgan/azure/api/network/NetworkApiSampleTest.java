package com.fgan.azure.api.network;

import ch.qos.logback.classic.Level;
import com.fgan.azure.LoggerAssert;
import com.fgan.azure.TestUtil;
import com.fgan.azure.api.network.exceptions.CreateNetworkException;
import com.fgan.azure.api.network.exceptions.CreateNetworkInterfaceException;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.util.AzureIDBuilderGeneral;
import com.fgan.azure.util.PropertiesUtil;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import rx.Completable;
import rx.Observable;

import java.io.IOException;

@PrepareForTest({PropertiesUtil.class, AzureIDBuilderGeneral.class})
public class NetworkApiSampleTest extends TestUtil {

    private LoggerAssert loggerAssertNetworkApiSample = new LoggerAssert(NetworkApiSample.class);

    private Azure azureMock;
    private NetworkApiSample networkApiSample;

    @Before
    public void setUp() throws IOException, AzureException.Unauthenticated {
        super.setUp();
        this.azureMock = null;
        this.networkApiSample = Mockito.spy(NetworkApiSample.build(azureMock));
    }

    @Test
    public void buildNetworkDeletionFogbowCompletableFailWhenThrowExceptionOnSecondOne() {
        // set up
        Completable mockCompletable = createMockCompletable();
        Completable mockCompletableError = createMockCompletableError();
        Mockito.doReturn(mockCompletableError).when(this.networkApiSample).buildDeleteNetworkInterfaceCompletable();
        Mockito.doReturn(mockCompletable).when(this.networkApiSample).buildDeleteNetworkCompletable();
        Mockito.doReturn(mockCompletable).when(this.networkApiSample).buildDeleteSecurityGroupCompletable();

        // execute
        try {
            Completable completable = this.networkApiSample.buildNetworkDeletionFogbowCompletable();
            completable.await();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        // verify
        this.loggerAssertNetworkApiSample
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_1)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_2)
                .assertEqualsInOrder(Level.ERROR, NetworkApiSample.NETWORK_DELETION_ERROR)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_3)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_4)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_5);
    }


    @Test
    public void buildNetworkDeletionFogbowCompletableFailWhenThrowExceptionOnSecondStep() {
        // set up
        Completable mockCompletable = createMockCompletable();
        Mockito.doReturn(mockCompletable).when(this.networkApiSample).buildDeleteNetworkInterfaceCompletable();
        Completable mockCompletableError = createMockCompletableError();
        Mockito.doReturn(mockCompletableError).when(this.networkApiSample).buildDeleteNetworkCompletable();
        Mockito.doReturn(mockCompletable).when(this.networkApiSample).buildDeleteSecurityGroupCompletable();

        // execute
        try {
            Completable completable = this.networkApiSample.buildNetworkDeletionFogbowCompletable();
            completable.await();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        // verify
        this.loggerAssertNetworkApiSample
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_1)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_2)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_3)
                .assertEqualsInOrder(Level.ERROR, NetworkApiSample.NETWORK_DELETION_ERROR)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_4)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_DELETION_STEP_5);
    }

    @Test
    public void testBuildNetworkCreationFogbowObservableSuccessful() {
        // set up
        NetworkSecurityGroup networkSecurityGroup = Mockito.mock(NetworkSecurityGroup.class);
        Observable<Indexable> observableSecurityGroup = Observable.just(networkSecurityGroup);
        Mockito.doReturn(observableSecurityGroup).when(this.networkApiSample).createSecurityGroupDefaultValues();

        Indexable networkIndexable = Mockito.mock(Indexable.class);
        Mockito.doReturn(networkIndexable).when(this.networkApiSample)
                .doNetworkCreationStepTwo(Mockito.eq(networkSecurityGroup));

        Mockito.doNothing().when(this.networkApiSample).doNetworkCreationStepThee(Mockito.eq(networkIndexable));

        // execute
        try {
            Observable<Indexable> completeNetworkFogbowStyle = this.networkApiSample.buildNetworkCreationFogbowObservable();
            completeNetworkFogbowStyle
                    .toBlocking()
                    .subscribe();
        } catch (CreateNetworkException e) {
            // exception expected
        } catch (Exception e) {
            Assert.fail();
        }

        // verify
        this.loggerAssertNetworkApiSample
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_1)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_2)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_3)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_4)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_5);
    }

    @Test
    public void testBuildNetworkCreationFogbowObservableFailWhenThrowNetworkException() {
        NetworkSecurityGroup networkSecurityGroup = Mockito.mock(NetworkSecurityGroup.class);
        Observable<Indexable> observableSecurityGroup = Observable.just(networkSecurityGroup);
        Mockito.doReturn(observableSecurityGroup).when(this.networkApiSample).createSecurityGroupDefaultValues();

        Mockito.doThrow(new CreateNetworkException()).when(this.networkApiSample)
                .doNetworkCreationStepTwo(Mockito.eq(networkSecurityGroup));

        Completable completable = createMockCompletable();
        Mockito.doReturn(completable).when(this.networkApiSample).getDeleteSecurityGroupCompletable();

        // execute
        try {
            Observable<Indexable> completeNetworkFogbowStyle = this.networkApiSample.buildNetworkCreationFogbowObservable();
            completeNetworkFogbowStyle
                    .toBlocking()
                    .subscribe();
        } catch (CreateNetworkException e) {
            // exception expected
        } catch (Exception e) {
            Assert.fail();
        }

        // verify
        this.loggerAssertNetworkApiSample
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_1)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_2)
                .assertEqualsInOrder(Level.ERROR, NetworkApiSample.NETWORK_CREATION_ERROR)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.START_CREATE_NETWORK_ROLLBACK)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.FINISH_CREATE_NETWORK_ROLLBACK);
    }

    @Test
    public void testBuildNetworkCreationFogbowObservableFailWhenThrowNetworkInterfaceException() {
        // set up
        NetworkSecurityGroup networkSecurityGroup = Mockito.mock(NetworkSecurityGroup.class);
        Observable<Indexable> observableSecurityGroup = Observable.just(networkSecurityGroup);
        Mockito.doReturn(observableSecurityGroup).when(this.networkApiSample).createSecurityGroupDefaultValues();

        Mockito.doThrow(new CreateNetworkInterfaceException()).when(this.networkApiSample)
                .doNetworkCreationStepTwo(Mockito.eq(networkSecurityGroup));

        Completable completable = createMockCompletable();
        Mockito.doReturn(completable).when(this.networkApiSample).getDeleteSecurityGroupCompletable();

        Completable completable2 = createMockCompletable();
        Mockito.doReturn(completable2).when(this.networkApiSample).getDeleteNetworkCompletable();

        // execute
        try {
            Observable<Indexable> completeNetworkFogbowStyle = this.networkApiSample.buildNetworkCreationFogbowObservable();
            completeNetworkFogbowStyle
                    .toBlocking()
                    .subscribe();
        } catch (CreateNetworkInterfaceException e) {
            // exception expected
        } catch (Exception e) {
            Assert.fail();
        }

        // verify
        this.loggerAssertNetworkApiSample
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_1)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.NETWORK_CREATION_STEP_2)
                .assertEqualsInOrder(Level.ERROR, NetworkApiSample.NETWORK_CREATION_ERROR)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.START_CREATE_NETWORK_ROLLBACK)
                .assertEqualsInOrder(Level.INFO, NetworkApiSample.FINISH_CREATE_NETWORK_ROLLBACK);
    }

    private Completable createMockCompletable() {
        return Completable.complete();
    }

    private Completable createMockCompletableError() {
        return Completable.error(new RuntimeException());
    }

}
