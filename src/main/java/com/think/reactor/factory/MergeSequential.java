package com.think.reactor.factory;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 和merge类似,唯一区别是排在前面的Publisher总是先返回数据
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:06:00
 */
public class MergeSequential {

    public static void main(String[] args) throws InterruptedException {
        Flux<String> flux1 = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2)).map(p -> "flux1 produce item: " + p).take(3);
        Flux<String> flux2 = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1)).map(p -> "flux2 produce item: " + p);

        Flux<String> mergedFlux = Flux.mergeSequential(flux1, flux2);
        mergedFlux.subscribe(System.out::println);
        Thread.sleep(5000);
    }
}
