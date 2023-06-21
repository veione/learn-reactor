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
 * @date 2023年06月21日 15:30:00
 */
public class BackPressureOnBackpressureLatest {

    public static void main(String[] args) {
        onBackPressureLatest();
    }

    private static void onBackPressureLatest() {
        Sinks.Many<String> hotSource = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> hotFlux = getHotFlux(hotSource);
        CompletableFuture future = produceData(hotSource);
        //构建Subscriber,初次请求20个元素
        BaseSubscriber<String> subscriber = createSubscriber(20);
        hotFlux.subscribe(subscriber);

        future.join();
        System.out.println("get reset elements");
        //再次获取10个元素,根据策略应返还最后10个元素
        subscriber.request(10);
    }

    private static Flux<String> getHotFlux(Sinks.Many<String> hotSource) {
        return hotSource
                .asFlux()
                .publish()
                .autoConnect()
                .onBackpressureBuffer();
    }

    private static CompletableFuture produceData(Sinks.Many<String> hotSource) {
        return CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50).forEach(
                    value -> {
                        hotSource.tryEmitNext("value is " + value);
                    }
            );
        });
    }

    private static BaseSubscriber<String> createSubscriber(int initRequests) {
        return new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(initRequests);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("get value " + value);
            }
        };
    }
}
