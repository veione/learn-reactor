package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 18:06:00
 */
public class ConcatWith {

    public static void main(String[] args) {
        Flux<String> just = Flux.just("a", "b", "c").concatWith(Flux.just("d", "e", "f"));
        StepVerifier.create(just)
                .expectNext("a")
                .expectNext("b")
                .expectNext("c")
                .expectNext("d")
                .expectNext("e")
                .expectNext("f")
                .verifyComplete();
    }
}
