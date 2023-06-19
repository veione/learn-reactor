package com.think.reactor;

import reactor.core.publisher.Flux;

import java.util.Arrays;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:00:00
 */
public class SimpleFlux {

    public static void main(String[] args) {
        Flux<String> stringFlow = Flux.just("one", "two", "three");
        Flux<Integer> numberFlow = Flux.fromIterable(Arrays.asList(1, 2, 3));
    }
}
