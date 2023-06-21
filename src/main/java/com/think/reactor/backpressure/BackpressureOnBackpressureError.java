package com.think.reactor.backpressure;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 16:18:00
 */
public class BackpressureOnBackpressureError {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        Sinks.Many<String> hotSource = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> hotFlux = hotSource
                .asFlux()
                .publish()
                .autoConnect()
                .onBackpressureError();

        CompletableFuture future = CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50).parallel().forEach(
                    value -> {
                        threadPool.submit(() -> hotSource.tryEmitNext("value is " + value));
                    }
            );
        });
        System.out.println("future run");

        hotFlux.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("get value " + value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        Thread.sleep(500);
        System.out.println("shutdown");
        threadPool.shutdownNow();
    }
}
