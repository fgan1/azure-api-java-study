package com.fgan.azure.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.fgan.azure.rx.Utils.*;

public class SampleMultRequestTraditional {

    public static void main(String[] args) throws InterruptedException {
        print("Start execution", SampleMultRequestTraditional.class, Thread.currentThread());
//        runSampleSingleThread();
        runSampleMultipleThread();
        print("Finish execution", SampleMultRequestTraditional.class, Thread.currentThread());
    }

    /*
    Using the same thread
     */
    public static void runSampleSingleThread() {
        Observable<Response> obsevableTest = createObsevable();
        obsevableTest.subscribe();
    }

    /*
    Using a new thread
     */
    public static void runSampleMultipleThread() {
        Observable<Response> obsevableTest = createObsevable();
        obsevableTest
                .subscribeOn(Schedulers.newThread())
                .subscribe();
        sleepOneSecond();
    }

    public static Observable<Response> createObsevable() {
        Observable<Response> firstRequestObservable = ObservablesCreator.createRequestObservable("One");
        return firstRequestObservable.flatMap((firstResponse) -> {

            String firstResponseStr = firstResponse.getResult();
            Observable<Response> secondRequestObservable = ObservablesCreator.createRequestObservable(firstResponseStr);
            return secondRequestObservable.flatMap((secondeResponse) -> {

                String secondeResponseStr = secondeResponse.getResult();
                Observable<Response> thirdRequestObservable = ObservablesCreator.createRequestObservable(secondeResponseStr);
                return thirdRequestObservable;
            });
        });
    }

}
