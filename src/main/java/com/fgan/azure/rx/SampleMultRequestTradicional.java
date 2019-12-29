package com.fgan.azure.rx;

import com.fgan.azure.rx.model.ObservablesCreator;
import com.fgan.azure.rx.model.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.fgan.azure.rx.util.Utils.print;
import static com.fgan.azure.rx.util.Utils.sleepOneSecond;

/*
Sample:
- Made 3 requests when 2 depends on the previous response.
-- Using traditional way.
-- Using flatMap.
 */
public class SampleMultRequestTradicional {

    public static void main(String[] args) {
        print("Run execution before", SampleMultRequestTradicional.class, Thread.currentThread());
        runSampleSingleThread();
//        runSampleMultipleThread();
        print("Run execution after", SampleMultRequestTradicional.class, Thread.currentThread());
        sleepOneSecond();
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
    }

    public static Observable<Response> createObsevable() {
        Observable<Response> firstRequestObservable = ObservablesCreator.createRequestObservable("First Request");
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
