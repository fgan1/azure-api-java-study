package com.fgan.azure.rx;

import rx.Completable;
import rx.Observable;

import static com.fgan.azure.rx.Utils.sleepOneSecond;

public class ObservablesCreator {

    public static Observable<Response> createRequestObservable(String requestName) {
        return Observable.defer(() -> {
            Request request = new Request(requestName);
            Response response = request.execute();
            return Observable.just(response);
        });
    }

    public static Observable<Response> createRequestStreamObservable(String requestName) {
        return Observable.create(emitter -> {
            Request request = new Request(requestName);
            Response response = request.execute();

            int count = 0;
            while (count < 2) {
                sleepOneSecond();
                count++;
                emitter.onNext(response);
            }

            emitter.onCompleted();
            emitter.unsubscribe();
        });
    }

    public static Completable createRequestNoResponseCompletable(String requestName) {
        return Completable.create((call) -> {
            Request request = new Request(requestName);
            request.execute();
            call.onCompleted();
        });
    }

}
