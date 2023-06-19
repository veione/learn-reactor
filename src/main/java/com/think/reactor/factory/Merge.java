package com.think.reactor.factory;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * merge和concat方法类似，只是不会依次返回每个Publisher流中的数据,
 * 而是哪个Publisher中先有数据生成，就立刻返回。如果发生异常，
 * 则会立刻抛出异常终止。
 * <p>
 * mergeDelayError会等到所有流都complete之后，再传播异常
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:02:00
 */
public class Merge {

    public static void main(String[] args) throws InterruptedException {
        Flux<String> flux1 = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2)).map(p -> "flux1 produce item: " + p);
        Flux<String> flux2 = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1)).map(p -> "flux2 produce item: " + p);

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);
        mergedFlux.subscribe(System.out::println);
        Thread.sleep(5000);
    }
}
