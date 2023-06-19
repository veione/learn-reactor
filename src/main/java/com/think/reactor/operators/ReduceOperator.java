package com.think.reactor.operators;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:26:00
 */
public class ReduceOperator {

    public static void main(String[] args) {
        Flux<Integer> source = Flux.range(0, 100);

        //使用迭代方式求和
        final AtomicInteger sum = new AtomicInteger(0);
        source.subscribe(integer -> {
            sum.getAndAdd(integer);
        });
        System.out.println(sum.get());

        //reduce方式求和
        source.reduce((i, j) -> i + j)
                .subscribe(System.out::println);

        //reduce方式求和,并且指定reduce的初始值
        source.reduce(100, (i, j) -> i + j)
                .subscribe(System.out::println);
    }
}
