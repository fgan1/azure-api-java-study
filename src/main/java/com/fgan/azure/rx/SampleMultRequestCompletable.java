package com.fgan.azure.rx;

import com.fgan.azure.rx.model.ObservablesCreator;
import rx.Completable;
import rx.schedulers.Schedulers;

import static com.fgan.azure.rx.util.Utils.print;
import static com.fgan.azure.rx.util.Utils.sleepOneSecond;

public class SampleMultRequestCompletable {

    public static void main(String[] args) {
        print("Run execution before", SampleMultRequestReadable.class, Thread.currentThread());
//        runSampleSingleThread();
//        runSampleMultipleThread();
//        runSampleInOrder();
        runSampleInOrderWithError();
        print("Run execution after", SampleMultRequestReadable.class, Thread.currentThread());
        sleepOneSecond();
    }

    /*
    Using the same thread
     */
    public static void runSampleSingleThread() {
        Completable obsevableTest = createObsevable();
        obsevableTest.subscribe();
    }

    /*
    Using a new thread
     */
    public static void runSampleMultipleThread() {
        Completable obsevableTest = createObsevable();
        obsevableTest
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    /*
    Using a new thread
     */
    public static void runSampleInOrder() {
        Completable first = ObservablesCreator.createRequestNoResponseCompletable("First Request");
        Completable second = ObservablesCreator.createRequestNoResponseCompletable("Second Request");

        first.doAfterTerminate(() -> {
            second.subscribe();
        }).subscribe();
    }

    /*
    Using a new thread
     */
    public static void runSampleInOrderWithError() {
        Completable first = Completable.error(new RuntimeException());
        Completable second = ObservablesCreator.createRequestNoResponseCompletable("Second Request");

        first.doAfterTerminate(() -> {
            second.subscribe();
        }).subscribe();
    }

    public static Completable createObsevable() {
        Completable firstRequestObservable = ObservablesCreator.createRequestNoResponseCompletable("First Request");

        return firstRequestObservable.doOnEach((firstResponse) -> {
            Completable secondRequestObservable = ObservablesCreator.createRequestNoResponseCompletable("Second Request");
            secondRequestObservable.await();

            Completable thirdRequestObservable = ObservablesCreator.createRequestNoResponseCompletable("Third Request");
            thirdRequestObservable.await();
        });
    }

}
