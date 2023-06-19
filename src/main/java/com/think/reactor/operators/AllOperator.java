package com.think.reactor.operators;

import reactor.core.publisher.Flux;

import java.util.function.Predicate;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 16:43:00
 */
public class AllOperator {

    public static void main(String[] args) {
        Flux<Integer> flux = Flux.range(0, 10).log();
        Predicate<Integer> allSmallerThan10 = integer -> integer < 10;
        flux.all(allSmallerThan10).log().subscribe();

        Predicate<Integer> allSmallerThan5 = integer -> integer < 5;
        flux.all(allSmallerThan5).log().subscribe();
    }
}
