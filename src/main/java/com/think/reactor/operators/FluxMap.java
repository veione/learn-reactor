package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:12:00
 */
public class FluxMap {

    public static void main(String[] args) {
        mapAfterPublishOn();
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");
        mapBeforeSubscribeOn();
    }

    /**
     * publishOn之后的operator都会使用指定的Scheduler
     * log()会打印出线程名
     */
    private static void mapAfterPublishOn() {
        Flux<Integer> flux = Flux.range(1, 100)
                .distinct()
                .publishOn(Schedulers.boundedElastic())
                .map(p -> p * 2)
                .log();
        flux.subscribe(System.out::println);
        flux.blockLast();
    }

    /**
     * subscribeOn之后的operator都会使用指定的Scheduler
     * log()会打印出线程名
     */
    private static void mapBeforeSubscribeOn() {
        Flux<Integer> flux = Flux.range(1, 100)
                .map(p -> p * 2)
                .subscribeOn(Schedulers.boundedElastic())
                .log();

        flux.subscribe(System.out::println);
        flux.blockLast();
    }
}
