package com.think.reactor.backpressure;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 15:51:00
 */
public class BackpressureOnBackpressureBuffer {

    public static void main(String[] args) throws InterruptedException {
        Sinks.Many<String> hotSource = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> hotFlux = hotSource
                .asFlux()
                .publish()
                .autoConnect()
                .onBackpressureBuffer(10);

        CompletableFuture future = CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50).forEach(
                    value -> {
                        hotSource.tryEmitNext("value is " + value);
                    }
            );
        });

        hotFlux.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(20);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("get value: " + value);
            }
        });
        future.thenRun(() -> hotSource.tryEmitComplete());
        future.join();
    }
}
