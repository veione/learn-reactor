package com.think.reactor.operators;

import reactor.core.publisher.Flux;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:09:00
 */
public class FlatMapOperator {

    public static void main(String[] args) {
        Flux<String> flux = Flux.just("a,b,c", "b,c,d", "a,c,d", "a,d,e")
                //每个元素都是一个Flux
                .map(str -> Flux.fromArray(str.split(",")))
                //展开为一个Flux流
                .flatMap(stringFlux -> stringFlux);
        Flux<String> distinct = flux.distinct();
        distinct.subscribe(System.out::println);
    }
}
