package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:01:00
 */
public class ElapsedOperator {

    public static void main(String[] args) {
        Flux<Integer> sourceFlux = Flux.range(0, 5)
                .map(n -> {
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return n;
                });

        Flux<Tuple2<Long, Integer>> timedFlux = sourceFlux.elapsed();
        timedFlux.log().subscribe();
    }
}
