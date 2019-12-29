package com.fgan.azure.rx;

import java.util.Random;

import static com.fgan.azure.rx.Utils.*;

public class Request {
    private static int count = 1;

    private String parameter;

    public Request(String parameter) {
        this.parameter = parameter;
    }

    public Response execute() {
        int responseStr = new Random().nextInt(10000);
        String message = String.format("REQUEST(%s) MADE WITH(%s) AND RESPONSE IS %s",
                count++, this.parameter, responseStr);
        print(message, this.getClass(), Thread.currentThread());
        return new Response(String.valueOf(responseStr));
    }
}
