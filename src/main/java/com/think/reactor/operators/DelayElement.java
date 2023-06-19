package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 18:09:00
 */
public class DelayElement {

    public static void main(String[] args) {
        Flux<Tuple2<Long, Integer>> just = Flux.just(0, 1, 2, 3, 4, 5)
                .elapsed()
                .doOnNext(value -> {
                    System.out.println(Thread.currentThread().getName() + " value = " + value);
                })
                //使用Scheduler,默认使用Schedulers.parallel()
                .delayElements(Duration.ofSeconds(1));
        just.subscribe(System.out::println);
        //由于使用了Scheduler,所以Flux和Subscriber运行的线程和主线程不同
        //此处必须blockLast,否则主线程退出，看不到打印数据
        just.blockLast();
    }
}
