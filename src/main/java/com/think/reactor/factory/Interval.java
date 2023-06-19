package com.think.reactor.factory;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * interval方法从给定的延迟时间然后每次间隔时间产出一个数据项，初始为0，并且随着间隔时间不断递增
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:59:00
 */
public class Interval {

    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1)).subscribe(System.out::println);
        Thread.sleep(5000);
    }
}
