package com.fgan.azure.rx;

import rx.Completable;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fgan.azure.rx.Utils.sleepOneSecond;

public class ObservablesCreator {

    public static Observable<Response> createRequestObservable(String requestName) {
        return Observable.defer(() -> {
            Request request = new Request(requestName);
            Response response = request.execute();
            return Observable.just(response);
        });
    }

    public static Observable<Response> createRequestObservableWithFuture(String requestName) {
        return Observable.create((emitter) -> {
            FutureRequest futureRequest = new FutureRequest();
            Request request = new Request(requestName);
            futureRequest.execute(request, emitter);
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

    public static class FutureRequest {

        private ExecutorService executor = Executors.newSingleThreadExecutor();

        public void execute(Request request, Subscriber<? super Response> emitter) {
            this.executor.submit(() -> {
                com.fgan.azure.rx.Response response = request.execute();
                emitter.onNext(response);
                emitter.onCompleted();
                emitter.unsubscribe();
            });
        }
    }

}
