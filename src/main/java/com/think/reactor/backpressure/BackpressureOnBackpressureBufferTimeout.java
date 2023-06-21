package com.think.reactor.backpressure;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 16:11:00
 */
public class BackpressureOnBackpressureBufferTimeout {

    public static void main(String[] args) throws InterruptedException {
        Sinks.Many<String> hotSource = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> hotFlux = hotSource.asFlux()
                .publish()
                .autoConnect()
                .publishOn(Schedulers.parallel())
                .onBackpressureBuffer(Duration.ofMillis(10), 5, s -> {
                    System.out.println("ttl " + s);
                });

        CompletableFuture future = CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50).forEach(
                    value -> {
                        hotSource.tryEmitNext("value is " + value);
                    }
            );
        });

        BaseSubscriber<String> subscriber = new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(5);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("get value " + value);
            }
        };
        hotFlux.subscribe(subscriber);
        future.join();
        Thread.sleep(1000);
        System.out.println("get reset elements from buffer");
        //再次获取10个元素,根据策略应返还最后的10个元素
        subscriber.request(10);
    }
}
