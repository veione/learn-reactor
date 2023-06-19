package com.think.reactor.operators;

import reactor.core.publisher.Flux;

import java.util.function.Predicate;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 16:45:00
 */
public class AnyOperator {

    public static void main(String[] args) {
        Flux<Integer> flux = Flux.range(0, 10).log();
        Predicate<Integer> anyBiggerThan9 = integer -> integer > 9;
        flux.any(anyBiggerThan9).subscribe();

        Predicate<Integer> anyBiggerThan5 = integer -> integer > 5;
        flux.any(anyBiggerThan5).subscribe();
    }
}
