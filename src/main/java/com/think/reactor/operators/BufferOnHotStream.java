package com.think.reactor.operators;

import com.think.reactor.coldhot.HotSourceExample;
import io.netty.util.concurrent.CompleteFuture;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 17:04:00
 */
public class BufferOnHotStream {

    public static void main(String[] args) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        Flux<String> hotFlux = sink.asFlux().publish().autoConnect().onBackpressureBuffer(10);

        CompletableFuture future = CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50).forEach(value -> {
                sink.tryEmitNext("value is " + value);
            });
        });

        hotFlux.buffer(5).subscribe(new BaseSubscriber<List<String>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(20);
            }

            @Override
            protected void hookOnNext(List<String> value) {
                System.out.println("get value " + value);
            }
        });
        future.thenRun(() -> sink.tryEmitComplete());
        future.join();
    }
}
