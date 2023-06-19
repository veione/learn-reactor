package com.think.reactor.operators;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:29:00
 */
public class SortOperator {

    public static void main(String[] args) {
        Flux<Long> flux = Flux.interval(Duration.ofMillis(100)).take(10).sort().log();
        flux.subscribe(
                System.out::print, System.err::println
        );
        flux.blockLast();
    }
}
