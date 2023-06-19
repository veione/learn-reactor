package com.think.reactor.operators;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:33:00
 */
public class WindowOperator {

    public static void main(String[] args) {
        Sinks.Many<String> hotSource = Sinks.many().multicast().onBackpressureBuffer(10);

        Flux<String> hotFlux = hotSource.asFlux().publish().autoConnect();

        CompletableFuture future = CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50).forEach(value -> {
                hotSource.tryEmitNext("value is " + value);
            });
        });

        hotFlux.window(5).subscribe(new BaseSubscriber<>() {
            int windowIndex = 0;
            int elementIndex = 0;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(20);
            }

            @Override
            protected void hookOnNext(Flux<String> value) {
                value.subscribe(new BaseSubscriber<String>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        System.out.println(String.format("Start window %d", windowIndex));

                        requestUnbounded();
                    }

                    @Override
                    protected void hookOnNext(String value) {
                        System.out.println(String.format("Element %d is %s", elementIndex, value));
                        elementIndex++;
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println(String.format("Finish window %d", windowIndex));
                        windowIndex++;
                        elementIndex = 0;
                    }
                });
            }
        });
        future.thenRun(() -> hotSource.tryEmitComplete());
        future.join();
    }
}
