package com.think.reactor.parallel;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * 并行
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 14:17:00
 */
public class FluxParallel {

    public static void main(String[] args) throws InterruptedException {
        Flux.range(1, 10)
                .parallel()
                .runOn(Schedulers.parallel())
                .subscribe(i -> {
                    System.out.println(Thread.currentThread().getName() + " -> " + i);
                });
        Thread.sleep(3000);
    }
}
