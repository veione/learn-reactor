package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 18:03:00
 */
public class CollectOps {
    private static Integer[] integers = {1, 2, 3, 4, 5, 1, 2, 3};

    public static void main(String[] args) {
        Flux<Integer> just = Flux.just(integers);

        //计算元素总数
        count(just);
    }

    private static void count(Flux<Integer> just) {
        StepVerifier.create(just.count())
                .expectNext((long) integers.length);
    }
}
