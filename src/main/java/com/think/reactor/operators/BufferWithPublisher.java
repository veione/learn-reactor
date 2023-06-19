package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * buffer入参包括了Publisher，publisher触发buffer中数据
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 17:12:00
 */
public class BufferWithPublisher {
    private static Flux<String> just = Flux.just("a", "b", "c", "d", "e");

    public static void main(String[] args) throws InterruptedException {
        Flux<String> source = Flux.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
                .delayElements(Duration.ofMillis(100));
        Mono<Long> other = Mono.delay(Duration.ofSeconds(1))
                .doOnNext(tick -> System.out.println("Tick!"));

        source.buffer(other)
                .subscribe(System.out::println, System.err::println);

        Thread.sleep(3000);
    }
}
