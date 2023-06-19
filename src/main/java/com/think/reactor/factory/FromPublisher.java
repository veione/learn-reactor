package com.think.reactor.factory;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * from方法通过给定的Publisher构造出一个Flux数据流
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:56:00
 */
public class FromPublisher {

    public static void main(String[] args) {
        Publisher<Integer> fluxPublisher = Flux.just(1, 2, 3);
        Publisher<Integer> monoPublisher = Mono.just(0);

        System.out.println("Flux from flux");
        Flux.from(fluxPublisher).subscribe(System.out::println);

        System.out.println("Flux from mono");
        Flux.from(monoPublisher).subscribe(System.out::println);
    }
}
